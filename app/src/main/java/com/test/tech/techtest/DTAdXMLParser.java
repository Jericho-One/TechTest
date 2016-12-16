package com.test.tech.techtest;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DTAdXMLParser {

    // use namespaces? not now
    private static final String NAME_SPACE = null;

    private static final String LIST_START_TAG = "ads";
    private static final String AD_START_TAG = "ad";
    public static final String NAME_VAL_SEPARATOR = "----";

    private static final String APP_ID_TAG = "appId";
    private static final String PRODUCT_ID_TAG = "productId";
    private static final String PRODUCT_NAME_TAG = "productName";
    private static final String PRODUCT_THUMBNAIL_TAG = "productThumbnail";
    private static final String PRODUCT_RATING_TAG = "rating";
    private static final String PRODUCT_LINK = "clickProxyURL";
    private static final String PRODUCT_DESCRIPTION = "productDescription";

    public List<Ad> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readAdList(parser);
        } finally {
            in.close();
        }
    }

    private List<Ad> readAdList(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Ad> adList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, NAME_SPACE, LIST_START_TAG);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals(AD_START_TAG)) {
                adList.add(readAd(parser));
            } else {
                skip(parser);
            }
        }
        return adList;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Ad readAd(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAME_SPACE, AD_START_TAG);
        String appId = null;
        String productID = null;
        String productName = null;
        String thumbNail = null;
        String productRating = null;
        String link = null;
        String productDescription = null;
        ArrayList<String> varargs = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case APP_ID_TAG:
                    appId = readValue(parser);
                    break;
                case PRODUCT_ID_TAG:
                    productID = readValue(parser);
                    break;
                case PRODUCT_NAME_TAG:
                    productName = readValue(parser);
                    break;
                case PRODUCT_THUMBNAIL_TAG:
                    thumbNail = readValue(parser);
                    break;
                case PRODUCT_RATING_TAG:
                    productRating = readValue(parser);
                    break;
                case PRODUCT_LINK:
                    link = readValue(parser);
                    break;
                case PRODUCT_DESCRIPTION:
                    productDescription = readValue(parser);
                    break;
                default:
                    if (!addToArgs(parser, name, varargs)) {
                        skip(parser);
                    }
            }

        }
        return hasRequiredFields(appId, productName, thumbNail, productRating, link) ?
                new Ad(appId, productName, thumbNail, productRating, link, productID, productDescription, varargs) :
                null;
    }

    private boolean addToArgs(XmlPullParser parser, String name, ArrayList<String> varargs) throws IOException, XmlPullParserException {
        StringBuilder result = new StringBuilder();
        if (parser.next() == XmlPullParser.TEXT) {
            result.append(name);
            result.append(NAME_VAL_SEPARATOR);
            result.append(parser.getText());
            varargs.add(result.toString());
            while (parser.getEventType() != XmlPullParser.END_TAG) {
                parser.next();
            }
            return true;
        }
        return false;
    }

    private boolean hasRequiredFields(String appId, String productName, String thumbNail, String productRating, String link) {
        return appId != null && productName != null && thumbNail != null && productRating != null && link != null;
    }

    private String readValue(XmlPullParser parser) throws IOException, XmlPullParserException{
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
