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

package net.yslibrary.requeryexample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.yslibrary.requeryexample.databinding.ActivityEditPersonBinding;
import net.yslibrary.requeryexample.model.Address;
import net.yslibrary.requeryexample.model.AddressEntity;
import net.yslibrary.requeryexample.model.Person;
import net.yslibrary.requeryexample.model.PersonEntity;
import net.yslibrary.requeryexample.model.Phone;
import net.yslibrary.requeryexample.model.PhoneEntity;

import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Simple activity allowing you to edit a Person entity using data binding.
 */
public class PersonEditActivity extends AppCompatActivity {

  static final String EXTRA_PERSON_ID = "personId";

  private SingleEntityStore<Persistable> data;
  private PersonEntity person;
  private ActivityEditPersonBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_person);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle("Edit Person");
    }
    data = ((PeopleApplication) getApplication()).getData();
    int personId = getIntent().getIntExtra(EXTRA_PERSON_ID, -1);
    if (personId == -1) {
      person = new PersonEntity(); // creating a new person
      binding.setPerson(person);
    } else {
      data.findByKey(PersonEntity.class, personId)
          .subscribeOn(AndroidSchedulers.mainThread())
          .subscribe(new Action1<PersonEntity>() {
            @Override
            public void call(PersonEntity person) {
              PersonEditActivity.this.person = person;
              binding.setPerson(person);
            }
          });
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_edit, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_save:
        savePerson();
        return true;
    }
    return false;
  }

  private void savePerson() {
    // TODO make binding 2 way
    person.setName(binding.name.getText().toString());
    person.setEmail(binding.email.getText().toString());
    Phone phone;
    if (person.getPhoneNumberList().isEmpty()) {
      phone = new PhoneEntity();
      phone.setOwner(person);
      person.getPhoneNumberList().add(phone);
    } else {
      phone = person.getPhoneNumberList().get(0);
    }
    phone.setPhoneNumber(binding.phone.getText().toString());
    Address address = person.getAddress();
    if (address == null) {
      address = new AddressEntity();
      person.setAddress(address);
    }
    address.setLine1(binding.street.getText().toString());
    address.setLine2(binding.city.getText().toString());
    address.setZip(binding.zip.getText().toString());
    address.setState(binding.state.getText().toString());
    // save the person
    if (person.getId() == 0) {
      data.insert(person).subscribe(new Action1<Person>() {
        @Override
        public void call(Person person) {
          finish();
        }
      });
    } else {
      data.update(person).subscribe(new Action1<Person>() {
        @Override
        public void call(Person person) {
          finish();
        }
      });
    }
  }
}
