/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blue.services.render;

import blue.BlueData;
import blue.event.PlayModeListener;
import blue.soundObject.SoundObjectException;
import java.util.List;

/**
 *
 * @author syi
 */
public interface RealtimeRenderService {

    void addPlayModeListener(PlayModeListener listener);

    boolean isRunning();

    void removePlayModeListener(PlayModeListener listener);

    void render() throws SoundObjectException;

    void renderForBlueLive() throws SoundObjectException;

    void setData(BlueData data);

    void stop();

    void passToStdin(String text);

    void addBinding(CsoundBinding binding);

    void removeBinding(CsoundBinding binding);

    void evalOrc(String orchestra);

}
