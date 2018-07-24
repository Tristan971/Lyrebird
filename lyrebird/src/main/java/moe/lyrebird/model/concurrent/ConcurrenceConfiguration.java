/*
 *     Lyrebird, a free open-source cross-platform twitter client.
 *     Copyright (C) 2017-2018, Tristan Deloche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package moe.lyrebird.model.concurrent;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * This class serves for the organization of all the background threads in the client application.
 */
@Configuration
public class ConcurrenceConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ConcurrenceConfiguration.class);

    /**
     * @return The executor for asynchronous io operations.
     */
    @Bean
    public Executor asyncIoExecutor() {
        final ThreadFactory asyncIoThreadFactory =
                new ThreadFactoryBuilder()
                        .setUncaughtExceptionHandler((t, e) -> ExceptionHandler.displayExceptionPane(
                                "Asynchronous IO loading issue",
                                "Could not load data in the background.",
                                e
                        ))
                        .setNameFormat("AsyncIO-%d")
                        .build();

        return Executors.newCachedThreadPool(asyncIoThreadFactory);
    }

    /**
     * @return The executor for cleanup operations execution.
     */
    @Bean
    public Executor cleanupExecutor() {
        final ThreadFactory cleanupThreadFactory =
                new ThreadFactoryBuilder()
                        .setUncaughtExceptionHandler((t, e) -> LOG.error(
                                "Exception on cleanup thread" + t.toString(),
                                e
                        ))
                        .setNameFormat("Cleanup-%d")
                        .build();

        return Executors.newSingleThreadExecutor(cleanupThreadFactory);
    }

    /**
     * @return The executor for asynchronous twitter network operations
     */
    @Bean
    public Executor twitterExecutor() {
        final ThreadFactory asyncTwitterThreadFactory =
                new ThreadFactoryBuilder()
                        .setUncaughtExceptionHandler((t, e) -> ExceptionHandler.displayExceptionPane(
                                "Asynchronous twitter query issue",
                                "Could not execute query in the background.",
                                e
                        ))
                        .setNameFormat("AsyncTwitter-%d")
                        .build();

        return Executors.newSingleThreadExecutor(asyncTwitterThreadFactory);
    }

    /**
     * @return The executor for update service operations
     */
    @Bean
    public ScheduledExecutorService updateExecutor() {
        final ThreadFactory updateThreadFactory =
                new ThreadFactoryBuilder()
                        .setUncaughtExceptionHandler((t, e) -> ExceptionHandler.displayExceptionPane(
                                "Asynchronous update data download issue",
                                "Could not execute query in the background.",
                                e
                        ))
                        .setNameFormat("Update-%d")
                        .build();

        return Executors.newScheduledThreadPool(2, updateThreadFactory);
    }

}
