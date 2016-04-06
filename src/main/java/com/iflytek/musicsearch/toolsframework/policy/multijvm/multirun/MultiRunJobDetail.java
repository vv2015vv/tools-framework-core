package com.iflytek.musicsearch.toolsframework.policy.multijvm.multirun;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.iflytek.musicsearch.toolsframework.config.TaskContext;
import com.iflytek.musicsearch.toolsframework.config.schema.MultiConfig;
import com.iflytek.musicsearch.toolsframework.policy.multijvm.TimerRunEntity;
import com.iflytek.musicsearch.toolsframework.spi.BusinessRun;
import com.iflytek.musicsearch.toolsframework.zookeeper.ZK_MultiRun;

public class MultiRunJobDetail implements Job {
	
	private static Logger logger = Logger.getLogger(MultiRunJobDetail.class);

	@Override
	public void execute(JobExecutionContext arg) throws JobExecutionException {
		String taskName = "";
		TimerRunEntity entity = null;
		TaskContext context = null;
		try{
			JobDataMap dataMap = arg.getJobDetail().getJobDataMap();
			context = (TaskContext)dataMap.get("JobContext");
			taskName = ((MultiConfig)context.getTaskConf()).getTaskname();
			
			//判断任务是否正在运行（在多个worker情况下，可能会有多个worker重复来运行，所以需要运行前检查任务的运行状态）
			entity = MultiRunCommon.getMapEntity().get(taskName);
			
			if(entity.isInException()){
				logger.info(String.format("任务：【%s】 zk链接处于异常中，本次循环不处理任何数据...", taskName));
				return;
			}
			
			if(context.isTaskStart()){
				logger.info(String.format("任务：【%s】 正在运行过程中...", taskName));
			}else{
				logger.info(String.format("任务：【%s】 开始运行...", taskName));
				context.setTaskStart(true);
				
				BusinessRun<?> runnable = entity.getRunnable();
				int threadCnt = entity.getThreadCnt();
				
				boolean canRedivde = false; //表示节点发生变化后，当前实例是否可以开始以新的len和index进行拆分
				boolean canRun = true; //在准备以新len和index拆分时，判断其他节点的标志值滞后，确定本周期是否能执行，否则要等待到下一个周期
				List<Object> list = new ArrayList<Object>();
				
				//在任务执行过程中，涉及到延迟和拆分变更的场景只有两个入口：1-刚启动   2-节点变化，两个入口分别带动其他的逻辑判断
				
				//工具刚启动时，按照如下步骤处理：
				//1、刚启动时，需要延迟一个周期，避免处于临界点的情况下，导致数据重复处理
				//2、一个周期之后，要判断其他节点的标志值是否为0，是的话则可以进行数据拆分，否则的话继续等待下一个周期
				if(entity.getNeedDelayExec()){
					logger.info(String.format("任务：【%s】 启动后需要延迟处理，本次循环休眠。。。", taskName));
					entity.setNeedDelayExec(false);
					entity.setNeedDelayAndCheckCurrCanRun(true);
				}else{
					if(entity.getNeedDelayAndCheckCurrCanRun()){
						logger.info(String.format("任务：【%s】 启动并延迟一个周期后，检查其他节点是否已经处理完毕。。。", taskName));
						if(ZK_MultiRun.judgeCurrBeanCanRun(taskName)){
							//如果可以运行的话就将该开关置为false
							entity.setNeedDelayAndCheckCurrCanRun(false);
						}else{
							canRun = false;
						}
					}else{
						//非刚刚启动的情况下：只要节点数量发生变化，都会按照以下顺序进行判断
						//1、发现changed，则以原始len和index进行分片
						//2、第二个周期时，检查其他节点的标志值是否为0（标志值指的是某个节点在Redivide之前是否已经把上一个周期的数据处理完成了）
						//3、上述两个条件满足时才能以新的len和index进行数据拆分
						//4、否则等待下一个周期继续判断其他节点的标志值
						//当上述流程处于中间状态时，节点数量又发生了变化，那么一切从头开始，从第一个流程判断hasChanged开始重新走流程
						if(entity.getHasChanged()){
							entity.setNeedReDivide(true);
							entity.setHasChanged(false);
							logger.info(String.format("任务：【%s】 发现有节点数量变更，本次按原节点数量和索引信息进行数据拆分。。。", taskName));
						}else{
							if(entity.getNeedReDivide()){
								logger.info(String.format("任务：【%s】 以新的len和index进行Redivide之前，检查其他节点的标志值。。。", taskName));
								if(ZK_MultiRun.judgeCurrBeanCanRun(taskName)){
									canRedivde = true;
									entity.setNeedReDivide(false);
								}else{
									canRun = false;
								}
							}
						}
					}
					if(canRun){
						if(canRedivde){
							logger.info(String.format("任务：【%s】本次拆分依据：len-%d  index-%d", taskName, entity.getCurrNodeCnt(), entity.getCurrIndex()));
							list = (List<Object>) runnable.divide(entity.getCurrIndex(), entity.getCurrNodeCnt());
							entity.setOriginalValue(entity.getCurrNodeCnt(), entity.getCurrIndex());
						}else{
							logger.info(String.format("任务：【%s】本次拆分依据：len-%d  index-%d", taskName, entity.getOriginalNodeCnt(), entity.getOriginalIndex()));
							list = (List<Object>) runnable.divide(entity.getOriginalIndex(), entity.getOriginalNodeCnt());
						}
						if(list.size() > 0){
							//logger.info(String.format("任务：【%s】 获取的数据为：%s", taskName, list.toString()));
							final Queue<Object> queue = new ConcurrentLinkedQueue<Object>(list);
							excuteBizTask(taskName, runnable, queue, threadCnt);
						}
						//之所以选择在redivide前的一个周期修改标志值，是为了避免多个实例间的死锁
						//避免出现A等B，B等A的情况
						if(entity.getNeedReDivide()){
							ZK_MultiRun.removeChildPathData(taskName);
							logger.info(String.format("任务：【%s】 节点变化后，在redivide之前，数据已处理完，将标志值置为0", taskName));
						}
					}else{
						logger.info(String.format("任务：【%s】 实例刚启动或者Redivide之前发现其他实例还处于运行状态，等待下一次循环检查。。。", taskName));
					}
				}
				
				//任务运行结束
				context.setTaskStart(false);
				logger.info(String.format("任务：【%s】 本次运行结束...", taskName));
			}
		}catch(Exception e){
			
			if(entity != null){
				context.setTaskStart(false);
			}
			
			// 遇到异常
			logger.error(String.format("任务：【%s】 本次运行出现异常...", taskName));
			logger.error(e);
			// 制造一个异常，让调度器知道，这样写有的噁心，暂时没有想到其他办法。
			String s = null;
			s.toString();
		}
	}
	
	/**
	 * 多线程调用业务端方法
	 * @param queue
	 * @param threadCnt
	 */
	@SuppressWarnings({"unchecked", "rawtypes" })
	private void excuteBizTask(final String taskName, final BusinessRun runnable, final Queue<Object> queue, int threadCnt){
		List<FutureTask<?>> tasks = new ArrayList<FutureTask<?>>();
		ExecutorService executor = MultiRunCommon.getMapExecutorService().get(taskName);
		for(int i=0; i<threadCnt; i++){
			Callable callable = new Callable() {
				@Override
				public Object call() throws Exception {
					while(queue.size() > 0){
						Object taskItem = queue.poll();
						if(taskItem != null){
							try {
								runnable.execute(taskItem);
								logger.info(String.format("任务：【%s】 的 item：【%s】处理完成", taskName, taskItem));
							} catch (Exception e) {
								logger.error(String.format("任务：【%s】 的 item：【%s】处理出现异常：", taskName, taskItem) + e);
							}
						}
					}
					return null;
				}
			};
			FutureTask<Object> future = new FutureTask<Object>(callable);
			executor.submit(future);
			tasks.add(future);
		}
		//多线程均需要阻塞等待执行完毕
		for (FutureTask<?> futureTask : tasks) {
			try {
				futureTask.get();
			}catch(Exception ex){
				ex.printStackTrace();
				logger.error("处理任务时出现异常" + ex);
			}
		}
	}
}
