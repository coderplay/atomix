/*
 * Copyright 2017-present Open Networking Foundation
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
package io.atomix.protocols.raft.partition.impl;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import io.atomix.cluster.MemberId;
import io.atomix.cluster.messaging.ClusterCommunicationService;
import io.atomix.raft.protocol.AppendRequest;
import io.atomix.raft.protocol.AppendResponse;
import io.atomix.raft.protocol.CommandRequest;
import io.atomix.raft.protocol.CommandResponse;
import io.atomix.raft.protocol.ConfigureRequest;
import io.atomix.raft.protocol.ConfigureResponse;
import io.atomix.raft.protocol.InstallRequest;
import io.atomix.raft.protocol.InstallResponse;
import io.atomix.raft.protocol.JoinRequest;
import io.atomix.raft.protocol.JoinResponse;
import io.atomix.raft.protocol.LeaveRequest;
import io.atomix.raft.protocol.LeaveResponse;
import io.atomix.raft.protocol.PollRequest;
import io.atomix.raft.protocol.PollResponse;
import io.atomix.raft.protocol.PublishRequest;
import io.atomix.raft.protocol.QueryRequest;
import io.atomix.raft.protocol.QueryResponse;
import io.atomix.raft.protocol.RaftServerProtocol;
import io.atomix.raft.protocol.ReconfigureRequest;
import io.atomix.raft.protocol.ReconfigureResponse;
import io.atomix.raft.protocol.TransferRequest;
import io.atomix.raft.protocol.TransferResponse;
import io.atomix.raft.protocol.VoteRequest;
import io.atomix.raft.protocol.VoteResponse;

import static io.atomix.utils.concurrent.Futures.uncheck;

/**
 * Raft server protocol that uses a {@link ClusterCommunicationService}.
 */
public class RaftServerCommunicator implements RaftServerProtocol {
  private final RaftMessageContext context;
  private final ClusterCommunicationService clusterCommunicator;

  public RaftServerCommunicator(ClusterCommunicationService clusterCommunicator) {
    this(null, clusterCommunicator);
  }

  public RaftServerCommunicator(String prefix, ClusterCommunicationService clusterCommunicator) {
    this.context = new RaftMessageContext(prefix);
    this.clusterCommunicator = Preconditions.checkNotNull(clusterCommunicator, "clusterCommunicator cannot be null");
  }

  private <T, U> CompletableFuture<U> sendAndReceive(
      String subject, T request, Function<T, byte[]> encoder, Function<byte[], U> decoder, MemberId memberId) {
    return clusterCommunicator.send(subject, request, encoder, decoder, MemberId.from(memberId.id()));
  }

  @Override
  public CompletableFuture<QueryResponse> query(MemberId memberId, QueryRequest request) {
    return sendAndReceive(
        context.querySubject,
        request,
        QueryRequest::toByteArray,
        uncheck(QueryResponse::parseFrom),
        memberId);
  }

  @Override
  public CompletableFuture<CommandResponse> command(MemberId memberId, CommandRequest request) {
    return sendAndReceive(
        context.commandSubject,
        request,
        CommandRequest::toByteArray,
        uncheck(CommandResponse::parseFrom),
        memberId);
  }

  @Override
  public CompletableFuture<JoinResponse> join(MemberId memberId, JoinRequest request) {
    return sendAndReceive(
        context.joinSubject,
        request,
        JoinRequest::toByteArray,
        uncheck(JoinResponse::parseFrom),
        memberId);
  }

  @Override
  public CompletableFuture<LeaveResponse> leave(MemberId memberId, LeaveRequest request) {
    return sendAndReceive(
        context.leaveSubject,
        request,
        LeaveRequest::toByteArray,
        uncheck(LeaveResponse::parseFrom),
        memberId);
  }

  @Override
  public CompletableFuture<ConfigureResponse> configure(MemberId memberId, ConfigureRequest request) {
    return sendAndReceive(
        context.configureSubject,
        request,
        ConfigureRequest::toByteArray,
        uncheck(ConfigureResponse::parseFrom),
        memberId);
  }

  @Override
  public CompletableFuture<ReconfigureResponse> reconfigure(MemberId memberId, ReconfigureRequest request) {
    return sendAndReceive(
        context.reconfigureSubject,
        request,
        ReconfigureRequest::toByteArray,
        uncheck(ReconfigureResponse::parseFrom),
        memberId);
  }

  @Override
  public CompletableFuture<InstallResponse> install(MemberId memberId, InstallRequest request) {
    return sendAndReceive(
        context.installSubject,
        request,
        InstallRequest::toByteArray,
        uncheck(InstallResponse::parseFrom),
        memberId);
  }

  @Override
  public CompletableFuture<TransferResponse> transfer(MemberId memberId, TransferRequest request) {
    return sendAndReceive(
        context.transferSubject,
        request,
        TransferRequest::toByteArray,
        uncheck(TransferResponse::parseFrom),
        memberId);
  }

  @Override
  public CompletableFuture<PollResponse> poll(MemberId memberId, PollRequest request) {
    return sendAndReceive(
        context.pollSubject,
        request,
        PollRequest::toByteArray,
        uncheck(PollResponse::parseFrom),
        memberId);
  }

  @Override
  public CompletableFuture<VoteResponse> vote(MemberId memberId, VoteRequest request) {
    return sendAndReceive(
        context.voteSubject,
        request,
        VoteRequest::toByteArray,
        uncheck(VoteResponse::parseFrom),
        memberId);
  }

  @Override
  public CompletableFuture<AppendResponse> append(MemberId memberId, AppendRequest request) {
    return sendAndReceive(
        context.appendSubject,
        request,
        AppendRequest::toByteArray,
        uncheck(AppendResponse::parseFrom),
        memberId);
  }

  @Override
  public void registerQueryHandler(Function<QueryRequest, CompletableFuture<QueryResponse>> handler) {
    clusterCommunicator.subscribe(
        context.querySubject,
        uncheck(QueryRequest::parseFrom),
        handler,
        QueryResponse::toByteArray);
  }

  @Override
  public void unregisterQueryHandler() {
    clusterCommunicator.unsubscribe(context.querySubject);
  }

  @Override
  public void registerCommandHandler(Function<CommandRequest, CompletableFuture<CommandResponse>> handler) {
    clusterCommunicator.subscribe(
        context.commandSubject,
        uncheck(CommandRequest::parseFrom),
        handler,
        CommandResponse::toByteArray);
  }

  @Override
  public void unregisterCommandHandler() {
    clusterCommunicator.unsubscribe(context.commandSubject);
  }

  @Override
  public void registerJoinHandler(Function<JoinRequest, CompletableFuture<JoinResponse>> handler) {
    clusterCommunicator.subscribe(
        context.joinSubject,
        uncheck(JoinRequest::parseFrom),
        handler,
        JoinResponse::toByteArray);
  }

  @Override
  public void unregisterJoinHandler() {
    clusterCommunicator.unsubscribe(context.joinSubject);
  }

  @Override
  public void registerLeaveHandler(Function<LeaveRequest, CompletableFuture<LeaveResponse>> handler) {
    clusterCommunicator.subscribe(
        context.leaveSubject,
        uncheck(LeaveRequest::parseFrom),
        handler,
        LeaveResponse::toByteArray);
  }

  @Override
  public void unregisterLeaveHandler() {
    clusterCommunicator.unsubscribe(context.leaveSubject);
  }

  @Override
  public void registerConfigureHandler(Function<ConfigureRequest, CompletableFuture<ConfigureResponse>> handler) {
    clusterCommunicator.subscribe(
        context.configureSubject,
        uncheck(ConfigureRequest::parseFrom),
        handler,
        ConfigureResponse::toByteArray);
  }

  @Override
  public void unregisterConfigureHandler() {
    clusterCommunicator.unsubscribe(context.configureSubject);
  }

  @Override
  public void registerReconfigureHandler(Function<ReconfigureRequest, CompletableFuture<ReconfigureResponse>> handler) {
    clusterCommunicator.subscribe(
        context.reconfigureSubject,
        uncheck(ReconfigureRequest::parseFrom),
        handler,
        ReconfigureResponse::toByteArray);
  }

  @Override
  public void unregisterReconfigureHandler() {
    clusterCommunicator.unsubscribe(context.reconfigureSubject);
  }

  @Override
  public void registerInstallHandler(Function<InstallRequest, CompletableFuture<InstallResponse>> handler) {
    clusterCommunicator.subscribe(
        context.installSubject,
        uncheck(InstallRequest::parseFrom),
        handler,
        InstallResponse::toByteArray);
  }

  @Override
  public void unregisterInstallHandler() {
    clusterCommunicator.unsubscribe(context.installSubject);
  }

  @Override
  public void registerTransferHandler(Function<TransferRequest, CompletableFuture<TransferResponse>> handler) {
    clusterCommunicator.subscribe(
        context.transferSubject,
        uncheck(TransferRequest::parseFrom),
        handler,
        TransferResponse::toByteArray);
  }

  @Override
  public void unregisterTransferHandler() {
    clusterCommunicator.unsubscribe(context.transferSubject);
  }

  @Override
  public void registerPollHandler(Function<PollRequest, CompletableFuture<PollResponse>> handler) {
    clusterCommunicator.subscribe(
        context.pollSubject,
        uncheck(PollRequest::parseFrom),
        handler,
        PollResponse::toByteArray);
  }

  @Override
  public void unregisterPollHandler() {
    clusterCommunicator.unsubscribe(context.pollSubject);
  }

  @Override
  public void registerVoteHandler(Function<VoteRequest, CompletableFuture<VoteResponse>> handler) {
    clusterCommunicator.subscribe(
        context.voteSubject,
        uncheck(VoteRequest::parseFrom),
        handler,
        VoteResponse::toByteArray);
  }

  @Override
  public void unregisterVoteHandler() {
    clusterCommunicator.unsubscribe(context.voteSubject);
  }

  @Override
  public void registerAppendHandler(Function<AppendRequest, CompletableFuture<AppendResponse>> handler) {
    clusterCommunicator.subscribe(
        context.appendSubject,
        uncheck(AppendRequest::parseFrom),
        handler,
        AppendResponse::toByteArray);
  }

  @Override
  public void unregisterAppendHandler() {
    clusterCommunicator.unsubscribe(context.appendSubject);
  }
}
