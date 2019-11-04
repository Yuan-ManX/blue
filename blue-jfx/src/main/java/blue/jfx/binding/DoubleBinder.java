/*
 * blue - object composition environment for csound
 * Copyright (C) 2015
 * Steven Yi <stevenyi@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package blue.jfx.binding;

import blue.jfx.BlueFX;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;

/**
 *
 * @author stevenyi
 */
public class DoubleBinder<T> {

    AtomicBoolean committing = new AtomicBoolean(false);
    private final WeakReference<TextField> textField;
    private WeakReference<DoubleProperty> doubleProperty = null;
    private BiFunction<T, Double, Double> filter;
    ChangeListener<? super Number> cl;
    private WeakReference<T> bean = null;

    public DoubleBinder(TextField tf) {
        this(tf, null);
    }

    public DoubleBinder(TextField tf, BiFunction<T, Double, Double> filter) {
        this.textField = new WeakReference<>(tf);
        this.filter = filter;

        tf.focusedProperty().addListener((obs, o, n) -> {
            if (o && !n) {
                BlueFX.runOnFXThread(
                        () -> updateValueFromTextFieldOrReset());
            }
        });
        tf.setOnAction(evt -> {
            BlueFX.runOnFXThread(
                    () -> updateValueFromTextFieldOrReset());
        });

        cl = (obs, oldVal, newVal) -> {
            TextField text = textField.get();
            if (text != null) {
                BlueFX.runOnFXThread(()
                        -> text.setText(newVal.toString())
                );
            }
        };
    }

    private void updateValueFromTextFieldOrReset() {
        DoubleProperty fp;
        TextField tf;

        if (doubleProperty == null
                || (fp = doubleProperty.get()) == null
                || (tf = textField.get()) == null) {
            return;
        }

        boolean inCommit = committing.getAndSet(true);
        try {
            if (!inCommit) {
                Double f = Double.parseDouble(tf.getText());
                if (filter != null) {
                    f = filter.apply(bean.get(), f);
                }
                if (f != null) {
                    fp.setValue(f);
                } else {
                    tf.setText(fp.getValue().toString());
                }
            }
        } catch (Exception e) {
            tf.setText(fp.getValue().toString());
        } finally {
            committing.set(false);
        }
    }

    public void setDoubleProperty(T bean, DoubleProperty prop) {
        TextField tf;
        DoubleProperty fp = doubleProperty == null ? null : doubleProperty.get();

        if ((tf = textField.get()) == null) {
            return;
        }

        if (fp != null) {
            fp.removeListener(cl);
        }

        this.doubleProperty = null;
        BlueFX.runOnFXThread(
                () -> tf.setText(prop.getValue().toString())
        );

        this.doubleProperty = new WeakReference<>(prop);
        prop.addListener(cl);

        this.bean = new WeakReference<>(bean);
    }

//    public void setFilter(BiFunction<T, Double, Double> filter) {
//        this.filter = filter;
//    }
}
