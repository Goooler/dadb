/*
 * Copyright (c) 2021 mobile.dev inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package dadb

import org.jetbrains.annotations.TestOnly
import java.io.File
import java.net.Socket

internal class DadbImpl(
        private val host: String,
        private val port: Int,
        private val keyPair: AdbKeyPair? = null
) : Dadb {

    private var connection: Pair<AdbConnection, Socket>? = null

    override fun shell(command: String) = connection().shell(command)
    override fun openShell(command: String) = connection().openShell(command)
    override fun install(file: File) = connection().install(file)
    override fun uninstall(packageName: String) = connection().uninstall(packageName)
    override fun abbExec(vararg command: String) = connection().abbExec(*command)
    override fun root() = connection().root()
    override fun unroot() = connection().unroot()
    override fun open(destination: String) = connection().open(destination)
    override fun close() = connection().unroot()
    override fun toString() = "$host:$port"

    @TestOnly
    fun closeConnection() {
        connection?.second?.close()
    }

    @Synchronized
    private fun connection(): AdbConnection {
        val connection = connection
        return if (connection == null || connection.second.isClosed) {
            val socket = Socket(host, port)
            AdbConnection.connect(socket, keyPair)
        } else {
            connection.first
        }
    }
}