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
import io.requery.query.MutableResult
import java.util.*

@Entity
interface Person : Observable, Parcelable, Persistable {

  @get:[Key Generated]
  val id: Int

  @get:Bindable
  val name: String

  @get:[Bindable Index(value = "email_index")]
  val email: String

  @get:Bindable
  val birthday: Date

  @get:Bindable
  val age: Int

  @get:[Bindable ForeignKey OneToOne]
  val address: Address

  @get:OneToMany(mappedBy = "owner", cascade = arrayOf(CascadeAction.DELETE, CascadeAction.SAVE))
  val phoneNumbers: MutableResult<Phone>

  @get:[Bindable Column(unique = true)]
  val uuid: UUID

  @get:OneToMany(mappedBy = "owner", cascade = arrayOf(CascadeAction.DELETE, CascadeAction.SAVE))
  val phoneNumberList: List<Phone>
}
