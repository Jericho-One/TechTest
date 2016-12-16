package com.test.tech.techtest;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AdvertFragment extends Fragment {

    private static final String KEY_ADVERT = "ADVERTISEMENT";
    public static final String TAG = "ADVERT_FRAG_TAG";

    public static AdvertFragment newInstance(Ad ad) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_ADVERT, ad);
        AdvertFragment fragment = new AdvertFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.ad_layout, container, false);
        Ad ad = getArguments().getParcelable(KEY_ADVERT);
        if (ad != null) {
            Context context = getActivity();

            if (ad.getimageURI() != null) {
                ImageView imageView = (ImageView) result.findViewById(R.id.product_image_view);
                imageView.setImageURI(Uri.parse(String.valueOf(ad.getimageURI())));
            }
            TextInputLayout product = (TextInputLayout) result.findViewById(R.id.product_name);
            populateViews(product, context.getString(R.string.product_name), ad.getName());

            TextInputLayout ratingView = (TextInputLayout) result.findViewById(R.id.product_rating);
            populateViews(ratingView, context.getString(R.string.product_rating), ad.getRating());

            displayOtherData(inflater, result, ad);
        }
        return result;
    }

    private void displayOtherData(LayoutInflater inflater, View view, Ad ad) {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.layout);
        for (String s : ad.getStrings()) {
            TextInputLayout v = (TextInputLayout) inflater.inflate(R.layout.floating_label_layout, null);
            String[] datas = s.split(DTAdXMLParser.NAME_VAL_SEPARATOR);
            if (datas.length == 2) {
                populateViews(v, datas[0], datas[1]);
                linearLayout.addView(v);
            }
        }
    }

    private void populateViews(TextInputLayout textInputLayout, String floatingLabelString, String text) {
        TextInputEditText textInputEditText = (TextInputEditText) textInputLayout.findViewById(R.id.text_input_edit_text);
        if (floatingLabelString != null) {
            textInputLayout.setHint(floatingLabelString);
        }
        textInputEditText.setText(text);
    }
}
