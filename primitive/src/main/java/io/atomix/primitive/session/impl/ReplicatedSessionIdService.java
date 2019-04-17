/*
 * Copyright 2018-present Open Networking Foundation
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
package io.atomix.primitive.session.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import io.atomix.primitive.operation.OperationId;
import io.atomix.primitive.operation.OperationType;
import io.atomix.primitive.operation.PrimitiveOperation;
import io.atomix.primitive.partition.PartitionGroup;
import io.atomix.primitive.session.ManagedSessionIdService;
import io.atomix.primitive.session.SessionClient;
import io.atomix.primitive.session.SessionId;
import io.atomix.primitive.session.SessionIdService;
import io.atomix.primitive.session.impl.proto.NextRequest;
import io.atomix.primitive.session.impl.proto.NextResponse;

import static io.atomix.utils.concurrent.Futures.uncheck;

/**
 * Replicated ID generator service.
 */
public class ReplicatedSessionIdService implements ManagedSessionIdService {
  private static final String PRIMITIVE_NAME = "session-id";

  private final PartitionGroup systemPartitionGroup;
  private SessionClient proxy;
  private final AtomicBoolean started = new AtomicBoolean();

  public ReplicatedSessionIdService(PartitionGroup systemPartitionGroup) {
    this.systemPartitionGroup = systemPartitionGroup;
  }

  @Override
  public CompletableFuture<SessionId> nextSessionId() {
    return proxy.execute(PrimitiveOperation.newBuilder()
        .setId(OperationId.newBuilder()
            .setName("NEXT")
            .setType(OperationType.COMMAND)
            .build())
        .setValue(NextRequest.newBuilder().build().toByteString())
        .build())
        .thenApply(uncheck(NextResponse::parseFrom))
        .thenApply(response -> SessionId.from(response.getSessionId()));
  }

  @Override
  public CompletableFuture<SessionIdService> start() {
    // TODO Open the proxy session
    return CompletableFuture.completedFuture(this);
  }

  @Override
  public boolean isRunning() {
    return started.get();
  }

  @Override
  public CompletableFuture<Void> stop() {
    return proxy.close()
        .exceptionally(v -> null)
        .thenRun(() -> started.set(false));
  }
}
