/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kuujo.mimeo;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

import net.kuujo.mimeo.cluster.ClusterConfig;
import net.kuujo.mimeo.log.Log;

/**
 * A node.
 *
 * @author Jordan Halterman
 */
public interface Node {

  /**
   * Sets the node address.
   *
   * @param address
   *   The node address.
   * @return
   *   The node instance.
   */
  Node setAddress(String address);

  /**
   * Returns the node address.
   *
   * @return
   *   The node address.
   */
  String getAddress();

  /**
   * Sets the cluster configuration.
   *
   * @param config
   *   The cluster configuration.
   * @return
   *   The node instance.
   */
  Node setClusterConfig(ClusterConfig config);

  /**
   * Returns the cluster configuration.
   *
   * @return
   *   The cluster configuration.
   */
  ClusterConfig getClusterConfig();

  /**
   * Sets the node election timeout.
   *
   * @param timeout
   *   The election timeout.
   * @return
   *   The node instance.
   */
  Node setElectionTimeout(long timeout);

  /**
   * Returns the node election timeout.
   *
   * @return
   *   The election timeout.
   */
  long getElectionTimeout();

  /**
   * Sets the node heartbeat interval.
   *
   * @param interval
   *   The interval at which the node should send heartbeat messages.
   * @return
   *   The node instance.
   */
  Node setHeartbeatInterval(long interval);

  /**
   * Returns the node heartbeat interval.
   *
   * @return
   *   The node heartbeat interval.
   */
  long getHeartbeatInterval();

  /**
   * Returns a boolean indicating whether adaptive timeouts are enabled.
   * 
   * @return Indicates whether adaptive timeouts are enabled.
   */
  public boolean isUseAdaptiveTimeouts();

  /**
   * Indicates whether the replica should use adaptive timeouts.
   * 
   * @param useAdaptive Indicates whether to use adaptive timeouts.
   * @return The replica.
   */
  public Node useAdaptiveTimeouts(boolean useAdaptive);

  /**
   * Returns the adaptive timeout threshold.
   * 
   * @return The adaptive timeout threshold.
   */
  public double getAdaptiveTimeoutThreshold();

  /**
   * Sets the adaptive timeout threshold.
   * 
   * @param threshold The adaptive timeout threshold.
   * @return The replica.
   */
  public Node setAdaptiveTimeoutThreshold(double threshold);

  /**
   * Returns a boolean indicating whether majority replication is required for
   * write operations.
   * 
   * @return Indicates whether majority replication is required for write
   *         operations.
   */
  public boolean isRequireWriteMajority();

  /**
   * Sets whether majority replication is required for write operations.
   * 
   * @param require Indicates whether majority replication should be required
   *          for writes.
   * @return The replica.
   */
  public Node setRequireWriteMajority(boolean require);

  /**
   * Returns a boolean indicating whether majority synchronization is required
   * for read operations.
   * 
   * @return Indicates whether majority synchronization is required for read
   *         operations.
   */
  public boolean isRequireReadMajority();

  /**
   * Sets whether majority synchronization is required for read operations.
   * 
   * @param require Indicates whether majority synchronization should be
   *          required for read operations.
   * @return The replica.
   */
  public Node setRequireReadMajority(boolean require);

  /**
   * Sets the replicated log.
   *
   * @param log
   *   The node's replicated log.
   * @return
   *   The node instance.
   */
  Node setLog(Log log);

  /**
   * Returns the replicated log.
   *
   * @return
   *   The node's replicated log.
   */
  Log getLog();

  /**
   * Registers a state machine command.
   *
   * @param commandName
   *   The command name.
   * @param function
   *   The command function.
   * @return
   *   The node instance.
   */
  <I, O> Node registerCommand(String commandName, Function<Command<I>, O> function);

  /**
   * Registers a typed state machine command.
   *
   * @param commandName
   *   The command name.
   * @param type
   *   The command type.
   * @param function
   *   The command function.
   * @return
   *   The node instance.
   */
  <I, O> Node registerCommand(String commandName, Command.Type type, Function<Command<I>, O> function);

  /**
   * Unregisters a state machine command.
   *
   * @param commandName
   *   The command name.
   * @return
   *   The node instance.
   */
  Node unregisterCommand(String commandName);

  /**
   * Submits a command to the replication service.
   * 
   * @param command The command to submit.
   * @param data The command data.
   * @param resultHandler An asynchronous handler to be called with the command
   *          result.
   * @return The replica.
   */
  <I, O> Node submitCommand(String command, I data, Handler<AsyncResult<O>> resultHandler);

  /**
   * Starts the node.
   *
   * @return
   *   The node instance.
   */
  Node start();

  /**
   * Starts the node.
   *
   * @param doneHandler
   *   An asynchronous handler to be called once the node has been started.
   * @return
   *   The node instance.
   */
  Node start(Handler<AsyncResult<Void>> doneHandler);

  /**
   * Stops the node.
   */
  void stop();

  /**
   * Stops the node.
   *
   * @param doneHandler
   *   An asynchronous handler to be called once the node has been stopped.
   */
  void stop(Handler<AsyncResult<Void>> doneHandler);

}
