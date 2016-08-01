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

package net.yslibrary.requeryexample.model


import android.databinding.Bindable
import android.databinding.Observable
import android.os.Parcelable
import io.requery.*

@Entity
interface Address : Observable, Parcelable, Persistable {

  @get:[Key Generated]
  val id: Int

  @get:Bindable
  var line1: String

  @get:Bindable
  var line2: String

  @get:Bindable
  var zip: String

  @get:Bindable
  var country: String

  @get:Bindable
  var city: String

  @get:[Bindable]
  var state: String

  @get:[Bindable OneToOne(mappedBy = "address")]
  val person: Person
}