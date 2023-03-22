/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.common.aspect;

import static com.google.common.collect.Lists.newArrayList;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * This can time spring beans methods.  
 * 
 * @author ksnd199
 */
@Aspect
@Component
@Order(20) // after caching at 10.  Dont want to log cached times 
public class TimeMeAspect extends TimeMeLog {

    @Around("within(@com.acuity.visualisations.common.aspect.TimeMe *)")
    public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();

        String classAndMethod = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
        logExecutionTime(classAndMethod, stopWatch, newArrayList(joinPoint.getArgs())); // log the method call

        return result;
    }
}
