package com.utng.discoverw;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SavesP}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SavesPRecyclerViewAdapter extends RecyclerView.Adapter<SavesPRecyclerViewAdapter.ViewHolder> {

    private final List<SavesP> mValues;
    private Context contexto;

    public SavesPRecyclerViewAdapter(Context ctx, List <SavesP> items) {
        contexto  = ctx;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_savesp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //Es el metodo que permite mostrar los componentes en el RecyclerView
        holder.mItem = mValues.get(position);
        holder.tvNombre.setText(holder.mItem.getNombre());
        holder.tvDescripcion.setText(holder.mItem.getDescripcion());
        holder.rbValoracion.setRating(holder.rbValoracion.getRating());

        Glide.with(contexto)
                .load(holder.mItem.getUrlPhoto())
                .centerCrop()
                .into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvNombre;
        public final TextView tvDescripcion;
        public final ImageView ivPhoto;
        public final RatingBar rbValoracion;
        public SavesP mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvNombre = (TextView) view.findViewById(R.id.tvNombre);
            tvDescripcion = (TextView) view.findViewById(R.id.tvDescripcion);
            ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
            rbValoracion = (RatingBar) view.findViewById(R.id.rbValoracion);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvNombre.getText() + "'";
        }
    }
}