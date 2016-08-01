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

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.requery.Persistable
import io.requery.rx.SingleEntityStore
import net.yslibrary.requeryexample.databinding.ActivityEditPersonBinding
import net.yslibrary.requeryexample.model.*
import rx.android.schedulers.AndroidSchedulers

/**
 * Simple activity allowing you to edit a Person entity using data binding.
 */
class PersonEditActivity : AppCompatActivity() {

  private lateinit var data: SingleEntityStore<Persistable>
  private lateinit var person: PersonEntity
  private lateinit var binding: ActivityEditPersonBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView<ActivityEditPersonBinding>(this, R.layout.activity_edit_person)
    if (supportActionBar != null) {
      supportActionBar!!.setTitle("Edit Person")
    }
    data = (application as PeopleApplication).data
    val personId = intent.getIntExtra(EXTRA_PERSON_ID, -1)
    if (personId == -1) {
      person = PersonEntity() // creating a new person
      binding.person = person
    } else {
      data.findByKey(PersonEntity::class.java, personId).subscribeOn(AndroidSchedulers.mainThread()).subscribe { person ->
        this@PersonEditActivity.person = person
        binding.person = person
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_edit, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_save -> {
        savePerson()
        return true
      }
    }
    return false
  }

  private fun savePerson() {
    // TODO make binding 2 way
    person.setName(binding.name.text.toString())
    person.setEmail(binding.email.text.toString())
    val phone: Phone
    if (person.phoneNumberList.isEmpty()) {
      phone = PhoneEntity()
      phone.owner = person
      person.phoneNumberList.add(phone)
    } else {
      phone = person.phoneNumberList.get(0)
    }
    phone.phoneNumber = binding.phone.text.toString()
    var address: Address? = person.address
    if (address == null) {
      address = AddressEntity()
      person.setAddress(address)
    }
    address.line1 = binding.street.text.toString()
    address.line2 = binding.city.text.toString()
    address.zip = binding.zip.text.toString()
    address.state = binding.state.text.toString()
    // save the person
    if (person.id == 0) {
      data.insert<PersonEntity>(person).subscribe { finish() }
    } else {
      data.update<PersonEntity>(person).subscribe { finish() }
    }
  }

  companion object {

    internal val EXTRA_PERSON_ID = "personId"
  }
}
