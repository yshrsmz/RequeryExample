/*
 * Copyright 2016 requery.io
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

package net.yslibrary.requeryexample

import android.app.Application
import android.os.StrictMode
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.rx.RxSupport
import io.requery.rx.SingleEntityStore
import io.requery.sql.EntityDataStore
import io.requery.sql.TableCreationMode
import net.yslibrary.requeryexample.model.Models

class PeopleApplication : Application() {

  private val dataStore: SingleEntityStore<Persistable> by lazy {
    val source = DatabaseSource(this, Models.DEFAULT, 1)
    if (BuildConfig.DEBUG) {
      // use this in development mode to drop and recreate the tables on every upgrade
      source.setTableCreationMode(TableCreationMode.DROP_CREATE)
    }
    val configuration = source.configuration
    RxSupport.toReactiveStore(EntityDataStore<Persistable>(configuration))
  }

  override fun onCreate() {
    super.onCreate()
    StrictMode.enableDefaults()
  }

  /**
   * @return [EntityDataStore] single instance for the application.
   * *
   *
   *
   * * Note if you're using Dagger you can make this part of your application level module returning
   * * `@Provides @Singleton`.
   */
  internal val data: SingleEntityStore<Persistable>
    get() = dataStore
}
