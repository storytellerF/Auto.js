package com.storyteller_f.bandage;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author storyteller_f
 */
public class Bandage {
    public static void bind(@NonNull Object ob, @NonNull View view1) {
        Method[] declaredMethods = ob.getClass().getDeclaredMethods();
        ArrayList<Method> arrayList = new ArrayList<>(Arrays.asList(declaredMethods));
        ArrayList<Method> to = new ArrayList<>();
        List<Class> annotationClass = new ArrayList<>(Arrays.asList(Click.class, OnTextChanged.class, OnCheckedChanged.class));
        for (Method method : arrayList) {
            for (Class aClass : annotationClass) {
                if (method.isAnnotationPresent(aClass)) {
                    to.add(method);
                    break;
                }
            }
        }
        arrayList.clear();
        HashMap<String, List<View>> viewHashMap = new HashMap<>();
        if (view1.getTag() != null)
            viewHashMap.put((String) view1.getTag(), Collections.singletonList(view1));
        if (view1 instanceof ViewGroup)
            handleTag(viewHashMap, (ViewGroup) view1);
        for (Method method : to) {
            if (method.isAnnotationPresent(Click.class))
                handleClick(ob, viewHashMap, method);
            if (method.isAnnotationPresent(OnTextChanged.class)) {
                handle(ob, viewHashMap, method);
            }
            if (method.isAnnotationPresent(OnCheckedChanged.class)) {
                OnCheckedChanged annotation = method.getAnnotation(OnCheckedChanged.class);
                String tag = annotation.tag();
                if (viewHashMap.containsKey(tag)) {
                    List<View> view = viewHashMap.get(tag);
                    for (View view2 : view) {
                        if (view2 instanceof CheckBox) {
                            ((CheckBox) view2).setOnCheckedChangeListener((buttonView, isChecked) -> {
                                try {
                                    method.invoke(ob);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                    }
                }
            }
        }
    }

    private static void handle(Object ob, @NonNull HashMap<String, List<View>> viewHashMap, @NonNull Method method) {
        OnTextChanged annotation = method.getAnnotation(OnTextChanged.class);
        String tag = annotation.tag();
        if (viewHashMap.containsKey(tag)) {
            List<View> view = viewHashMap.get(tag);
            for (View view2 : view) {
                if (view2 instanceof EditText) {
                    ((EditText) view2).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            try {
                                method.invoke(ob);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }
        }
    }

    private static void handleClick(Object ob, @NonNull HashMap<String, List<View>> viewHashMap, @NonNull Method method) {
        Click annotation = method.getAnnotation(Click.class);
        String tag = annotation.tag();
        if (viewHashMap.containsKey(tag)) {
            List<View> view = viewHashMap.get(tag);
            for (View view2 : view) {
                view2.setOnClickListener(v -> {
                    try {
                        method.invoke(ob);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private static void handleTag(@NonNull HashMap<String, List<View>> viewHashMap, @NonNull ViewGroup viewGroup) {
        if (viewGroup.getTag() != null) {
            viewHashMap.put(viewGroup.getTag().toString(), Collections.singletonList(viewGroup));
        }
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                handleTag(viewHashMap, (ViewGroup) childAt);
            } else if (childAt.getTag() != null) {
                Object tag = childAt.getTag();
                if (tag != null) {
                    if (viewHashMap.containsKey(tag)) {
                        viewHashMap.get(tag).add(childAt);
                    } else {
                        List<View> views = new ArrayList<>();
                        views.add(childAt);
                        viewHashMap.put((String) tag, views);
                    }
                }
            }
        }
    }
}
