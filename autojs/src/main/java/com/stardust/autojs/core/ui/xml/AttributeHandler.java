package com.stardust.autojs.core.ui.xml;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/5/14.
 */

public interface AttributeHandler {

    boolean handle(String nodeName, Node attr, StringBuilder layoutXml);

    class AttrNameRouter implements AttributeHandler {

        private final Map<String, AttributeHandler> mHandlerMap = new HashMap<>();
        private AttributeHandler mDefaultHandler;

        @Override
        public boolean handle(String nodeName, @NonNull Node attr, StringBuilder layoutXml) {
            AttributeHandler handler = mHandlerMap.get(attr.getNodeName());
            if (handler == null)
                handler = mDefaultHandler;
            return handler != null && handler.handle(nodeName, attr, layoutXml);
        }

        @NonNull
        public AttrNameRouter handler(String attrName, AttributeHandler handler) {
            mHandlerMap.put(attrName, handler);
            return this;
        }

        @NonNull
        public AttrNameRouter defaultHandler(AttributeHandler defaultHandler) {
            mDefaultHandler = defaultHandler;
            return this;
        }
    }

    class MappedAttributeHandler implements AttributeHandler {

        private final Map<String, String> mAttrNameMap = new HashMap<>();
        private final Map<String, Map<String, String>> mAttrValueMap = new HashMap<>();

        @Override
        public boolean handle(String nodeName, @NonNull Node attr, @NonNull StringBuilder layoutXml) {
            if (!attr.getNodeName().equals("style")) {
                layoutXml.append("android:");
            }
            layoutXml.append(mapAttrName(nodeName, attr.getNodeName()))
                    .append("=\"").append(mapAttrValue(nodeName, attr.getNodeName(), attr.getNodeValue())).append("\"\n");
            return true;
        }

        @NonNull
        public MappedAttributeHandler mapName(String oldAttrName, String newAttrName) {
            mAttrNameMap.put(oldAttrName, newAttrName);
            return this;
        }

        @NonNull
        public MappedAttributeHandler mapValue(String attrName, String oldValue, String newValue) {
            Map<String, String> valueMap = mAttrValueMap.get(attrName);
            if (valueMap == null) {
                valueMap = new HashMap<>();
                mAttrValueMap.put(attrName, valueMap);
            }
            valueMap.put(oldValue, newValue);
            return this;
        }


        @Nullable
        private String mapAttrName(String nodeName, String attrName) {
            String mappedAttrName = mAttrNameMap.get(attrName);
            if (mappedAttrName == null)
                return attrName;
            return mappedAttrName;
        }

        private String mapAttrValue(String nodeName, String attrName, String value) {
            Map<String, String> valueMap = mAttrValueMap.get(attrName);
            if (valueMap == null)
                return value;
            String mappedValue = valueMap.get(value);
            return mappedValue == null ? value : mappedValue;
        }
    }

    class IdHandler implements AttributeHandler {

        @Override
        public boolean handle(String nodeName, @NonNull Node attr, @NonNull StringBuilder layoutXml) {
            layoutXml.append("android:id=\"@+id/").append(attr.getNodeValue()).append("\"\n");
            return true;
        }
    }

    class DimenHandler implements AttributeHandler {

        private final String mAttrName;

        public DimenHandler(String attrName) {
            mAttrName = attrName;
        }

        @NonNull
        static String convertToAndroidDimen(@NonNull String dimen) {
            if (dimen.equals("*")) {
                return "match_parent";
            }
            if (dimen.equals("auto")) {
                return "wrap_content";
            }
            if (Character.isDigit(dimen.charAt(dimen.length() - 1))) {
                return dimen + "dp";
            }
            return dimen;
        }

        @Override
        public boolean handle(String nodeName, @NonNull Node attr, @NonNull StringBuilder layoutXml) {
            String dimen = convertToAndroidDimen(attr.getNodeValue());
            layoutXml.append("android:").append(mAttrName).append("=\"").append(dimen).append("\"\n");
            return true;
        }
    }

    class OrientationHandler implements AttributeHandler {

        @Override
        public boolean handle(String nodeName, @NonNull Node attr, @NonNull StringBuilder layoutXml) {
            if (attr.getNodeValue().equals("true")) {
                layoutXml.append("android:orientation=\"vertical\"\n");
            } else if (attr.getNodeValue().equals("false")) {
                layoutXml.append("android:orientation=\"horizontal\"\n");
            } else {
                return false;
            }
            return true;
        }
    }

    class MarginPaddingHandler implements AttributeHandler {

        private final String mAttrName;

        public MarginPaddingHandler(String attrName) {
            mAttrName = attrName;
        }

        @Override
        public boolean handle(String nodeName, @NonNull Node attr, @NonNull StringBuilder layoutXml) {
            String[] intervals = attr.getNodeValue().split("[ ,]");
            String[] dimens = new String[intervals.length];
            for (int i = 0; i < intervals.length; i++) {
                dimens[i] = DimenHandler.convertToAndroidDimen(intervals[i]);
            }
            String left, top, right, bottom;
            switch (dimens.length) {
                case 1:
                    left = top = right = bottom = dimens[0];
                    break;
                case 2:
                    top = bottom = dimens[0];
                    left = right = dimens[1];
                    break;
                case 3:
                    top = dimens[0];
                    left = right = dimens[1];
                    bottom = dimens[2];
                    break;
                case 4:
                    top = dimens[0];
                    right = dimens[1];
                    bottom = dimens[2];
                    left = dimens[3];
                    break;
                default:
                    return false;
            }
            layoutXml.append("android:").append(mAttrName).append("Top=\"").append(top).append("\"\n")
                    .append("android:").append(mAttrName).append("Right=\"").append(right).append("\"\n")
                    .append("android:").append(mAttrName).append("Bottom=\"").append(bottom).append("\"\n")
                    .append("android:").append(mAttrName).append("Left=\"").append(left).append("\"\n");
            return true;
        }

    }
}
