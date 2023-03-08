package awesome.zaza.cat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context ctx;
    ArrayList<String> words;
    ArrayList<Integer> visibility;

    RecyclerViewInterface rvi;


    public RecyclerViewAdapter(Context ctx, ArrayList<String> words, ArrayList<Integer> visibility, RecyclerViewInterface rvi) {
        this.ctx = ctx;
        this.words = words;
        this.visibility = visibility;
        this.rvi = rvi;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(ctx);
        View view = li.inflate(R.layout.word_item, parent, false);
        return new RecyclerViewAdapter.ViewHolder(view, rvi);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.tv.setText(words.get(position));

        if (visibility.get(position) == 0) {
            holder.tv.setVisibility(View.INVISIBLE);
        } else {
            holder.tv.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface rvi) {
            super(itemView);
            iv = itemView.findViewById(R.id.imageView);
            tv = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener( view -> {
                if (rvi != null){
                    rvi.onItemClick();
                }
            });
        }
    }
}