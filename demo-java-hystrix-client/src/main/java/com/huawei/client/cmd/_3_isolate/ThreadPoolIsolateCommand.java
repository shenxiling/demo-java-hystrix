package com.huawei.client.cmd._3_isolate;

import com.huawei._1_fw.HttpUtilsEx;
import com.huawei.client.vo.SimpleDemoResVo;
import com.huawei.client.vo.SimpleDemoVo;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class ThreadPoolIsolateCommand extends HystrixCommand<SimpleDemoResVo> {
	// #region Fields

	private SimpleDemoVo simpleDemoVo;

	// #endregion

	// #region Construction

	public ThreadPoolIsolateCommand(SimpleDemoVo oSimpleDemoVo) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ThreadPoolIsolateCommand"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("1"))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ThreadPoolIsolatePool"))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
						// 1.thread pool
						// Thread Timeout in ThreadPool????
						.withCoreSize(10)
						// 1.1
						.withMaximumSize(10)
						// 1.2
						.withMaxQueueSize(-1)
						// 1.3.?
						.withKeepAliveTimeMinutes(1)
						// 1.4.?
						.withAllowMaximumSizeToDivergeFromCoreSize(false))
				.andCommandPropertiesDefaults(
						HystrixCommandProperties.Setter()
								// 2.strategy
								.withExecutionIsolationStrategy(
										HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
								// 3.task timeout
								.withExecutionTimeoutInMilliseconds(5000).withExecutionTimeoutEnabled(true)
								// 4.threadInterrupt
								.withExecutionIsolationThreadInterruptOnTimeout(true)
								.withExecutionIsolationThreadInterruptOnFutureCancel(false)
								// 5.fallback
								.withFallbackEnabled(true)
								// 6.circuit break
								.withCircuitBreakerRequestVolumeThreshold(5)
								// 6.1
								.withCircuitBreakerEnabled(true)
								// 6.2.?
								.withCircuitBreakerForceOpen(false)
								// 6.3.?
								.withCircuitBreakerForceClosed(false)
								// 6.4.熔断后多长时间开始重试，确定熔断是否可关闭(default value:5000)
								.withCircuitBreakerSleepWindowInMilliseconds(10000)
								// 6.5.?
								.withCircuitBreakerErrorThresholdPercentage(50)));
		this.simpleDemoVo = oSimpleDemoVo;
	}

	// #endregion

	// #region run

	@Override
	protected SimpleDemoResVo run() throws Exception {
		System.out.println("buData:" + this.simpleDemoVo.getName());

		SimpleDemoResVo oSimpleDemoResVo = HttpUtilsEx.postByTemplate("http://localhost:8000/hystrix/server/crazyfunc",
				this.simpleDemoVo, SimpleDemoResVo.class);
		return oSimpleDemoResVo;
	}

	// #endregion

	// #region fallback

	@Override
	protected SimpleDemoResVo getFallback() {
		System.out.println("fallBack:" + this.simpleDemoVo.getName());

		// TODO:replace by websocket
		SimpleDemoResVo oSimpleDemoResVo = new SimpleDemoResVo();
		oSimpleDemoResVo.setName("fallback");
		return oSimpleDemoResVo;
	}

	// #endregion
}
