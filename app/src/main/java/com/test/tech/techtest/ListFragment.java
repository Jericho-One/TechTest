package com.test.tech.techtest;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "MAIN_FRAGMENT";

    private static final String KEY_LIST = "ads_list";
    private Callbacks callbacks;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerListAdapter adapter;

    public static ListFragment newInstance(List<Ad> ads) {
        ListFragment result = new ListFragment();
        if (ads instanceof ArrayList) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(KEY_LIST, (ArrayList<? extends Parcelable>) ads);
            result.setArguments(bundle);
        }
        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_main, container, false);
        setUpSwipeRefresh(result);
        setUpRecyclerView(result);
        setupToolbar(result);
        return result;
    }

    private void setUpSwipeRefresh(View view) {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_recycler_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        setSwipeRefreshLayout(swipeRefreshLayout);
    }

    private void setSwipeRefreshLayout(@NonNull SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.list_view);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private void setUpRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        List<Ad> ads = getArguments().getParcelableArrayList(KEY_LIST);
        adapter = new RecyclerListAdapter(getActivity(), ads);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        callbacks.refresh();
    }

    public void update(List<Ad> result) {
        if (result instanceof ArrayList) {
            getArguments().putParcelableArrayList(KEY_LIST, (ArrayList<? extends Parcelable>) result);
            adapter.update(result);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    protected static class RecyclerListAdapter extends RecyclerView.Adapter<ListViewHolder> {

        private Context context;
        private List<Ad> ads;

        public RecyclerListAdapter(Context context, List<Ad> ads) {
            super();
            this.context = context;
            this.ads = ads;
        }

        @Override
        public ListViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.list_item_with_image, parent, false);
            final ListViewHolder result = new ListViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ad ad = ads.get(result.getAdapterPosition());
                    ((Callbacks)context).onItemClick(ad);
                }
            });
            return result;
        }

        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {
            Ad ad = ads.get(position);
            ImageView imageView = (ImageView) holder.imageView.findViewById(R.id.product_image);
            if (ad.getimageURI() == null) {
                new DownloadImageTask(context, ad, holder.parent).execute(ad.getImage());
                imageView.setVisibility(View.INVISIBLE);
            } else {
                imageView.setImageURI((Uri.parse(String.valueOf(ad.getimageURI()))));
            }
            holder.parent.findViewById(R.id.image_progress).setVisibility(View.VISIBLE);
            holder.title.setText(ad.getName());
            holder.rating.setText(ad.getRating());
        }

        @Override
        public int getItemCount() {
            return ads.size();
        }

        public void update(List<Ad> ads) {
            this.ads = ads;
            notifyDataSetChanged();
        }
    }

    protected static class ListViewHolder extends RecyclerView.ViewHolder {
        View parent;
        ImageView imageView;
        TextView title;
        TextView rating;

        public ListViewHolder(View itemView) {
            super(itemView);
            parent = itemView;
            title = (TextView) itemView.findViewById(R.id.product_name);
            rating = (TextView) itemView.findViewById(R.id.product_rating);
            imageView = (ImageView) itemView.findViewById(R.id.product_image);
        }

    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Context context;
        Ad ad;
        View layout;

        public DownloadImageTask(Context context, Ad ad, View layout) {
            this.context = context;
            this.ad = ad;
            this.layout = layout;
        }

        protected Bitmap doInBackground(String... urls) {
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(ad, result);
            ImageView image = (ImageView)layout.findViewById(R.id.product_image);
            image.setImageBitmap(result);
            image.setVisibility(View.VISIBLE);
            layout.findViewById(R.id.image_progress).setVisibility(View.GONE);
        }

        private void saveImage(Ad ad, Bitmap result) {
            String filname = ad.getAppId() + "-photo/";
            File file;
            file = new File(context.getFilesDir(), filname);
            Uri outputFileUri = Uri.fromFile(file);
            ad.setImageURI(outputFileUri);
            cachePhoto(result, file);
        }
    }

    public static void cachePhoto(Bitmap bitmap, File file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface Callbacks {
        void refresh();
        void onItemClick(Ad ad);
    }
}
