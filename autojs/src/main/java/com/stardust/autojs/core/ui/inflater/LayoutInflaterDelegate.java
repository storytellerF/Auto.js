package com.stardust.autojs.core.ui.inflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.stardust.autojs.core.ui.inflater.inflaters.ViewGroupInflater;

import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2018/3/29.
 */

public interface LayoutInflaterDelegate {

    LayoutInflaterDelegate NO_OP = new NoOp();

    @Nullable
    View beforeInflation(InflateContext inflateContext, String xml, ViewGroup parent);

    View afterInflation(InflateContext inflateContext, View doInflation, String xml, ViewGroup parent);

    @Nullable
    String beforeConvertXml(InflateContext inflateContext, String xml);

    String afterConvertXml(InflateContext inflateContext, String xml);

    @Nullable
    View beforeInflateView(InflateContext inflateContext, Node node, ViewGroup parent, boolean attachToParent);

    View afterInflateView(InflateContext inflateContext, View view, Node node, ViewGroup parent, boolean attachToParent);

    @Nullable
    View beforeCreateView(InflateContext inflateContext, Node node, String viewName, ViewGroup parent, HashMap<String, String> attrs);

    View afterCreateView(InflateContext inflateContext, View view, Node node, String viewName, ViewGroup parent, HashMap<String, String> attrs);

    boolean beforeApplyAttributes(InflateContext inflateContext, View view, ViewInflater<View> inflater, HashMap<String, String> attrs, ViewGroup parent);

    void afterApplyAttributes(InflateContext inflateContext, View view, ViewInflater<View> inflater, HashMap<String, String> attrs, ViewGroup parent);

    boolean beforeInflateChildren(InflateContext inflateContext, ViewInflater<View> inflater, Node node, ViewGroup parent);

    void afterInflateChildren(InflateContext inflateContext, ViewInflater<View> inflater, Node node, ViewGroup parent);

    void afterApplyPendingAttributesOfChildren(InflateContext inflateContext, ViewGroupInflater inflater, ViewGroup view);

    boolean beforeApplyPendingAttributesOfChildren(InflateContext inflateContext, ViewGroupInflater inflater, ViewGroup view);

    boolean beforeApplyAttribute(InflateContext inflateContext, ViewInflater<View> inflater, View view, String ns, String attrName, String value, ViewGroup parent, Map<String, String> attrs);


    void afterApplyAttribute(InflateContext inflateContext, ViewInflater<View> inflater, View view, String ns, String attrName, String value, ViewGroup parent, Map<String, String> attrs);


    class NoOp implements LayoutInflaterDelegate {
        @Nullable
        @Override
        public String beforeConvertXml(InflateContext inflateContext, String xml) {
            return null;
        }

        @Override
        public String afterConvertXml(InflateContext inflateContext, String xml) {
            return xml;
        }

        @Override
        public View afterInflation(InflateContext inflateContext, View result, String xml, ViewGroup parent) {
            return result;
        }

        @Nullable
        @Override
        public View beforeInflation(InflateContext inflateContext, String xml, ViewGroup parent) {
            return null;
        }

        @Nullable
        @Override
        public View beforeInflateView(InflateContext inflateContext, Node node, ViewGroup parent, boolean attachToParent) {
            return null;
        }

        @Override
        public View afterInflateView(InflateContext inflateContext, View view, Node node, ViewGroup parent, boolean attachToParent) {
            return view;
        }

        @Nullable
        @Override
        public View beforeCreateView(InflateContext inflateContext, Node node, String viewName, ViewGroup parent, HashMap<String, String> attrs) {
            return null;
        }

        @Override
        public View afterCreateView(InflateContext inflateContext, View view, Node node, String viewName, ViewGroup parent, HashMap<String, String> attrs) {
            return view;
        }

        @Override
        public boolean beforeApplyAttributes(InflateContext inflateContext, View view, ViewInflater<View> inflater, HashMap<String, String> attrs, ViewGroup parent) {
            return false;
        }

        @Override
        public void afterApplyAttributes(InflateContext inflateContext, View view, ViewInflater<View> inflater, HashMap<String, String> attrs, ViewGroup parent) {

        }

        @Override
        public boolean beforeInflateChildren(InflateContext inflateContext, ViewInflater<View> inflater, Node node, ViewGroup parent) {
            return false;
        }

        @Override
        public void afterInflateChildren(InflateContext inflateContext, ViewInflater<View> inflater, Node node, ViewGroup parent) {

        }

        @Override
        public void afterApplyPendingAttributesOfChildren(InflateContext inflateContext, ViewGroupInflater inflater, ViewGroup view) {

        }

        @Override
        public boolean beforeApplyPendingAttributesOfChildren(InflateContext inflateContext, ViewGroupInflater inflater, ViewGroup view) {
            return false;
        }

        @Override
        public boolean beforeApplyAttribute(InflateContext inflateContext, ViewInflater<View> inflater, View view, String ns, String attrName, String value, ViewGroup parent, Map<String, String> attrs) {
            return false;
        }

        @Override
        public void afterApplyAttribute(InflateContext inflateContext, ViewInflater<View> inflater, View view, String ns, String attrName, String value, ViewGroup parent, Map<String, String> attrs) {

        }
    }
}
