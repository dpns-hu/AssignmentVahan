    package com.example.assignment_vahan

    import android.content.Context
    import android.content.Intent
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.recyclerview.widget.RecyclerView
    import com.example.assignment_vahan.databinding.RecyclerviewDesignBinding

    class MyAdapter(var context : Context, var list: ArrayList<Items>    ):RecyclerView.Adapter<MyAdapter.viewHolder>() {


        inner class viewHolder(binding:RecyclerviewDesignBinding): RecyclerView.ViewHolder(binding.root){
           var country = binding.countryId
            var university = binding.universityNameId
            var visit = binding.visitId

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
           return viewHolder(RecyclerviewDesignBinding.inflate(LayoutInflater.from(context),parent,false))
        }

        override fun getItemCount(): Int {
         return list.size
        }

        override fun onBindViewHolder(holder: viewHolder, position: Int) {
          holder.country.text = list[position].country
            holder.university.text = list[position].name
            holder.visit.setOnClickListener{
                val url = list[position].web_pages[0]
                 val intent = Intent(context,webviewActivity::class.java)
                intent.putExtra("url",url)
                context.startActivity(intent)
            }

        }

    }