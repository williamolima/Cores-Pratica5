package com.example.minhascores

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val ADDCOLOR = 1
private const val EDITCOLOR = 2
class MainActivity : AppCompatActivity() {
    private lateinit var fbAdd: FloatingActionButton
    private lateinit var lvColor: ListView
    private lateinit var collors: ArrayList<Collor>
    private lateinit var dao: ColorDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.fbAdd = findViewById(R.id.fbMainAdd)
        this.lvColor = findViewById(R.id.lvMainColors)

        this.dao = ColorDAO(this)
        this.collors = dao.get()

        this.lvColor.adapter = ListViewAdapter(this, this.collors)

        lvColor.onItemClickListener = EditColor()
        lvColor.onItemLongClickListener = RemoveColor()

        fbAdd.setOnClickListener {
            startActivityForResult(Intent(this, FormActivity::class.java), ADDCOLOR)
        }
    }

    inner class EditColor: AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val color = this@MainActivity.collors[position]
            val intent = Intent(this@MainActivity, FormActivity::class.java)
            intent.putExtra("EDIT_COLOR", color)
            startActivityForResult(intent, EDITCOLOR)
        }
    }

    inner class RemoveColor: AdapterView.OnItemLongClickListener {
        override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
            val color = this@MainActivity.collors[position]
            this@MainActivity.dao.delete(color.id)
            (this@MainActivity.lvColor.adapter as ListViewAdapter).remove(color)
            return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK){
            if (requestCode == ADDCOLOR){
                val color = data?.getSerializableExtra("COLOR_SAVE") as Collor
                this.dao.insert(color)
                (this.lvColor.adapter as ListViewAdapter).add(color)
            } else if (requestCode == EDITCOLOR) {
                val color = data?.getSerializableExtra("COLOR_SAVE") as Collor
                for (c in this.collors) {
                    if (c.id == color.id) {
                        c.name = color.name
                        c.code = color.code
                        this.dao.update(c)
                        (this.lvColor.adapter as ListViewAdapter).update()
                        break
                    }
                }
            }
        }
    }
}