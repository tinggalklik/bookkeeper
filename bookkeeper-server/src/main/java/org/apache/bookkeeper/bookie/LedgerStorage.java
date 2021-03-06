/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.apache.bookkeeper.bookie;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;

import org.apache.bookkeeper.bookie.CheckpointSource.Checkpoint;

/**
 * Interface for storing ledger data
 * on persistant storage.
 */
interface LedgerStorage {

    /**
     * Register a listener on ledger storage changes.
     *
     * @param listener listener on ledger storage changes.
     */
    void registerListener(LedgerStorageListener listener);

    /**
     * Reclaim disk space from ledger storage.
     *
     * @throws IOException
     */
    void reclaimDiskSpace() throws IOException;

    /**
     * Start any background threads
     * belonging to the storage system. For example,
     * garbage collection.
     */
    void start();

    /**
     * Cleanup and free any resources
     * being used by the storage system.
     */
    void shutdown() throws InterruptedException;

    /**
     * Whether a ledger exists
     */
    boolean ledgerExists(long ledgerId) throws IOException;

    /**
     * Fenced the ledger id in ledger storage.
     *
     * @param ledgerId
     *          Ledger Id.
     * @throws IOException when failed to fence the ledger.
     */
    boolean setFenced(long ledgerId) throws IOException;

    /**
     * Check whether the ledger is fenced in ledger storage or not.
     *
     * @param ledgerId
     *          Ledger ID.
     * @throws IOException
     */
    boolean isFenced(long ledgerId) throws IOException;

    /**
     * Set the master key for a ledger
     */
    void setMasterKey(long ledgerId, byte[] masterKey) throws IOException;

    /**
     * Get the master key for a ledger
     * @throws IOException if there is an error reading the from the ledger
     * @throws BookieException if no such ledger exists
     */
    byte[] readMasterKey(long ledgerId) throws IOException, BookieException;

    /**
     * Add an entry to the storage.
     * @return the entry id of the entry added
     */
    long addEntry(ByteBuffer entry) throws IOException;

    /**
     * Read an entry from storage
     */
    ByteBuffer getEntry(long ledgerId, long entryId) throws IOException;

    /**
     * Get last add confirmed.
     *
     * @param ledgerId
     *          ledger id.
     * @return last add confirmed.
     * @throws IOException
     */
    long getLastAddConfirmed(long ledgerId) throws IOException;

    /**
     * Wait for last add confirmed update.
     *
     * @param previoisLAC - The threshold beyond which we would wait for the update
     * @param observer  - Observer to notify on update
     * @return
     * @throws IOException
     */
    Observable waitForLastAddConfirmedUpdate(long ledgerId, long previoisLAC, Observer observer) throws IOException;

    /**
     * Flushes all data in the storage. Once this is called,
     * add data written to the LedgerStorage up until this point
     * has been persisted to permanent storage
     */
    void flush() throws IOException;

    /**
     * Ask the ledger storage to sync data until the given <i>checkpoint</i>.
     * The ledger storage implementation do checkpoint and return the real checkpoint
     * that it finished. The returned checkpoint indicates that all entries added
     * before that point already persist.
     *
     * @param checkpoint
     *          Check Point that {@link CheckpointSource} proposed.
     * @throws IOException
     * @return the checkpoint that the ledger storage finished
     */
    Checkpoint checkpoint(Checkpoint checkpoint) throws IOException;

}
