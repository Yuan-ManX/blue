/*
 * blue - object composition environment for csound
 * Copyright (C) 2013
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
package blue.csnd6.render;

import blue.services.render.DiskRenderService;
import blue.services.render.DiskRenderServiceFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author stevenyi
 */
@ServiceProvider(service = DiskRenderServiceFactory.class, position = 100)
public class CS6DiskRenderServiceFactory implements DiskRenderServiceFactory {

    @Override
    public DiskRenderService createInstance() {
        return new CS6DiskRendererService();
    }

    @Override
    public boolean isAvailable() {
        return API6Utilities.isCsoundAPIAvailable();
    }
    
    @Override 
    public String toString() {
        return "Csound 6 API";
    }
}
