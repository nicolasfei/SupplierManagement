package com.nicolas.supplier.advertisement;

import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class AdvertViewHolderCreator extends BannerAdapter<AdvertData, AdvertViewHolderCreator.AdvertViewHolder> {


    public AdvertViewHolderCreator(List<AdvertData> datas) {
        super(datas);
    }

    @Override
    public AdvertViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new AdvertViewHolder(imageView);
    }

    @Override
    public void onBindView(AdvertViewHolder holder, AdvertData data, int position, int size) {
        holder.imageView.setImageBitmap(data.getImageRes());
    }

    public class AdvertViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public AdvertViewHolder(ImageView itemView) {
            super(itemView);
            this.imageView = itemView;
        }
    }
}
