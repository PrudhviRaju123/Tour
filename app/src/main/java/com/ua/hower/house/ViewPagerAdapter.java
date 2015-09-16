package com.ua.hower.house;

/**
 * Created by Prudhvi on 3/26/2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ViewPagerAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    double[] image_order ;
    String[] image_caption;
    String[] image_description;
    String[] image_data;
    LayoutInflater inflater;

    public ViewPagerAdapter(Context context, String[] image_data, String[] image_caption,
                            String[] image_description, double[] image_order) {
        this.context = context;
        this.image_caption = image_caption;
        this.image_description = image_description;
        this.image_data = image_data;
        this.image_order =image_order;
    }

    @Override
    public int getCount() {


        if(image_order ==null) return 0 ;
            else
        return image_order.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ScrollView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // Declare Variables
        TextView imageName, imageDescription;
        ImageView image;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.page_item, container,
                false);


        image = (ImageView) itemView.findViewById(R.id.imageView);
        imageName = (TextView) itemView.findViewById(R.id.ImageNameTextview);
        imageDescription = (TextView) itemView.findViewById(R.id.ImageDescTextview);



        Typeface domFont = FontCache.get("Domine-Regular.ttf",context);
        Typeface fjFont = FontCache.get("FjallaOne-Regular.ttf",context);

        imageName.setTypeface(fjFont);
        imageDescription.setTypeface(domFont);
        imageName.setPaintFlags(imageName.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        imageDescription.setPaintFlags(imageName.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        // Capture position and set to the TextViews

        imageName.setText(image_caption[position]);
        imageDescription.setText(image_description[position]);
        //image.setImageResource(image_data[position]);

        Bitmap bitmap = BitmapFactory.decodeFile(image_data[position]);
        image.setImageBitmap(bitmap);


        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((ScrollView) object);

    }

}
