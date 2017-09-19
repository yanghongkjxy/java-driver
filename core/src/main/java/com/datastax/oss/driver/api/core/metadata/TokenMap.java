/*
 * Copyright (C) 2017-2017 DataStax Inc.
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
package com.datastax.oss.driver.api.core.metadata;

import com.datastax.oss.driver.api.core.Cluster;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.ProtocolVersion;
import com.datastax.oss.driver.api.core.config.CoreDriverOption;
import com.datastax.oss.driver.api.core.metadata.token.Token;
import com.datastax.oss.driver.api.core.metadata.token.TokenRange;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import java.nio.ByteBuffer;
import java.util.Set;

/**
 * Utility component to work with the tokens of a given driver instance.
 *
 * <p>Note that the methods that take a keyspace argument are based on schema metadata, which can be
 * disabled or restricted to a subset of keyspaces; therefore these methods might return empty
 * results for some or all of the keyspaces.
 *
 * @see CoreDriverOption#METADATA_SCHEMA_ENABLED
 * @see Cluster#setSchemaMetadataEnabled(Boolean)
 * @see CoreDriverOption#METADATA_SCHEMA_REFRESHED_KEYSPACES
 */
public interface TokenMap {

  /** Builds a token from its string representation. */
  Token newToken(String tokenString);

  /**
   * Builds a token from a partition key.
   *
   * @param partitionKey the partition key components, in their serialized form (which can be
   *     obtained with {@link TypeCodec#encode(Object, ProtocolVersion)}
   */
  Token newToken(ByteBuffer... partitionKey);

  TokenRange newTokenRange(Token start, Token end);

  /** The token ranges that define data distribution on the ring. */
  Set<TokenRange> getTokenRanges();

  /** The token ranges that are replicated on the given node, for the given keyspace. */
  Set<TokenRange> getTokenRanges(CqlIdentifier keyspace, Node replica);

  /** The replicas for a given partition key in the given keyspace. */
  Set<Node> getReplicas(CqlIdentifier keyspace, ByteBuffer partitionKey);

  /**
   * The replicas for a given range in the given keyspace.
   *
   * <p>It is assumed that the input range does not overlap across multiple node ranges. If the
   * range extends over multiple nodes, it only returns the nodes that are replicas for the last
   * token of the range.
   */
  Set<Node> getReplicas(CqlIdentifier keyspace, TokenRange range);
}