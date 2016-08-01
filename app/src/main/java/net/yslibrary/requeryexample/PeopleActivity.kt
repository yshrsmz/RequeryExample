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

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import io.requery.Persistable
import io.requery.android.QueryRecyclerAdapter
import io.requery.query.Result
import io.requery.rx.SingleEntityStore
import net.yslibrary.requeryexample.databinding.PersonItemBinding
import net.yslibrary.requeryexample.model.Person
import net.yslibrary.requeryexample.model.PersonEntity
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Activity displaying a list of random people. You can tap on a person to edit their record.
 * Shows how to use a query with a [RecyclerView] and [QueryRecyclerAdapter] and RxJava
 */
class PeopleActivity : AppCompatActivity() {

  private lateinit var data: SingleEntityStore<Persistable>
  private lateinit var executor: ExecutorService
  private lateinit var adapter: PersonAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (supportActionBar != null) {
      supportActionBar!!.setTitle("People")
    }
    setContentView(R.layout.activity_people)
    val recyclerView = findViewById(R.id.recyclerView) as RecyclerView
    data = (application as PeopleApplication).data
    executor = Executors.newSingleThreadExecutor()
    adapter = PersonAdapter()
    adapter.setExecutor(executor)
    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(this)

    data.count(Person::class.java).get().toSingle()
        .subscribe { integer ->
          if (integer === 0) {
            Observable.fromCallable(CreatePeople(data))
                .observeOn(Schedulers.computation())
                .flatMap { iterableObservable -> iterableObservable }
                .subscribe(
                    { adapter.queryAsync() },
                    { Log.e("PeopleActivity", it.message, it) })
          } else {
            val p1 = data.select(Person::class.java).get().first()
            data.select(Person::class.java).where(PersonEntity.ID.eq(p1.id)).get()
                .toSelfObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { persons -> Log.d("Person Query", persons.first().address.state) }
          }
        }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_plus -> {
        val intent = Intent(this, PersonEditActivity::class.java)
        startActivity(intent)
        return true
      }
    }
    return false
  }

  override fun onResume() {
    adapter.queryAsync()
    super.onResume()
  }

  override fun onDestroy() {
    executor.shutdown()
    adapter.close()
    super.onDestroy()
  }

  private inner class PersonAdapter internal constructor() : QueryRecyclerAdapter<PersonEntity, BindingHolder<PersonItemBinding>>(PersonEntity.`$TYPE`), View.OnClickListener {

    private val random = Random()
    private val colors = intArrayOf(Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA)

    override fun performQuery(): Result<PersonEntity> {
      // this is all persons in the db sorted by their name
      // note this method in executed in a background thread.
      // (Alternatively RxJava w/ RxBinding could be used)
      return data.select(PersonEntity::class.java).orderBy(PersonEntity.NAME.lower()).get()
    }

    override fun onBindViewHolder(item: PersonEntity, holder: BindingHolder<PersonItemBinding>,
                                  position: Int) {
      holder.binding.person = item
      holder.binding.picture.setBackgroundColor(colors[random.nextInt(colors.size)])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder<PersonItemBinding> {
      val inflater = LayoutInflater.from(parent.context)
      val binding = PersonItemBinding.inflate(inflater)
      binding.root.tag = binding
      binding.root.setOnClickListener(this)
      return BindingHolder(binding)
    }

    override fun onClick(v: View) {
      val binding = v.tag as PersonItemBinding
      if (binding != null) {
        val intent = Intent(this@PeopleActivity, PersonEditActivity::class.java)
        intent.putExtra(PersonEditActivity.EXTRA_PERSON_ID, binding.person.id)
        startActivity(intent)
      }
    }
  }
}
