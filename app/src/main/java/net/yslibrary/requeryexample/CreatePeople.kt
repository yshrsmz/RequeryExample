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

import io.requery.Persistable
import io.requery.rx.SingleEntityStore
import net.yslibrary.requeryexample.model.AddressEntity
import net.yslibrary.requeryexample.model.Person
import net.yslibrary.requeryexample.model.PersonEntity
import rx.Observable
import java.util.*
import java.util.concurrent.Callable

internal class CreatePeople(private val data: SingleEntityStore<Persistable>) : Callable<Observable<Iterable<Person>>> {

  override fun call(): Observable<Iterable<Person>> {
    val firstNames = arrayOf("Alice", "Bob", "Carol", "Chloe", "Dan", "Emily", "Emma", "Eric", "Eva", "Frank", "Gary", "Helen", "Jack", "James", "Jane", "Kevin", "Laura", "Leon", "Lilly", "Mary", "Maria", "Mia", "Nick", "Oliver", "Olivia", "Patrick", "Robert", "Stan", "Vivian", "Wesley", "Zoe")
    val lastNames = arrayOf("Hall", "Hill", "Smith", "Lee", "Jones", "Taylor", "Williams", "Jackson", "Stone", "Brown", "Thomas", "Clark", "Lewis", "Miller", "Walker", "Fox", "Robinson", "Wilson", "Cook", "Carter", "Cooper", "Martin")
    val random = Random()

    val people = TreeSet(Comparator<Person> { lhs, rhs -> lhs.name.compareTo(rhs.name) })
    // creating many people (but only with unique names)
    for (i in 0..2999) {
      val person = PersonEntity()
      val first = firstNames[random.nextInt(firstNames.size)]
      val last = lastNames[random.nextInt(lastNames.size)]
      person.setName(first + " " + last)
      person.setUuid(UUID.randomUUID())
      person.setEmail(Character.toLowerCase(first[0]) +
          last.toLowerCase() + "@gmail.com")
      val address = AddressEntity()
      address.line1 = "123 Market St"
      address.zip = "94105"
      address.city = "San Francisco"
      address.state = "CA"
      address.country = "US"
      person.setAddress(address)
      people.add(person)
    }
    return data.insert(people).toObservable()
  }
}
