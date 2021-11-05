package com.flycloud.autofly.map.layer;

import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.data.TileKey;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.internal.jni.CoreImageTiledLayer;
import com.esri.arcgisruntime.io.RemoteResource;
import com.esri.arcgisruntime.io.RequestConfiguration;
import com.esri.arcgisruntime.layers.ImageTiledLayer;
import com.esri.arcgisruntime.security.Credential;
import com.flycloud.autofly.base.util.FileUtils;
import com.flycloud.autofly.map.util.TileImageDownloadUtils;

import java.io.File;


public class TiledMapLayer extends ImageTiledLayer implements RemoteResource {

    private MapLayerInfo layerInfo;
    private String cachePath;

    public TiledMapLayer(MapLayerInfo mapLayerInfo) {
        this(mapLayerInfo, null);
    }

    public TiledMapLayer(MapLayerInfo mapLayerInfo, String cachePath) {
        super(mapLayerInfo.getTileInfo(), mapLayerInfo.getEnvelope());
        this.layerInfo = mapLayerInfo;
        this.cachePath = cachePath;
    }

    protected TiledMapLayer(TileInfo tileInfo, Envelope fullExtent) {
        super(tileInfo, fullExtent);
    }


    protected TiledMapLayer(CoreImageTiledLayer coreTiledLayer, boolean addToCache) {
        super(coreTiledLayer, addToCache);
    }


    @Override
    protected byte[] getTile(TileKey tileKey) {
        byte[] iResult = null;

        try {

            String fileName = null;
            if (cachePath != null) {
              
                File directory = new File(cachePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String newFileName = cachePath + File.separator + String.format(
                        "L%dR%dC%d", tileKey.getLevel(), tileKey.getRow(), tileKey.getColumn());

                fileName = newFileName;

                File newFileImage = new File(newFileName);
                if (newFileImage.exists()) {
                    return FileUtils.getBytesFromFile(newFileImage);
                }

                String oldFileName = cachePath + File.separator + String.format(
                        "L%dR%dC%d.png", tileKey.getLevel(), tileKey.getRow(), tileKey.getColumn());
                File oldFileImage = new File(oldFileName);
                if (oldFileImage.exists()) {
                    return FileUtils.getBytesFromFile(oldFileImage);
                }
            }
            iResult = TileImageDownloadUtils.getImageFromURL2(LayerInfoFactory.getLayerUrl(layerInfo, tileKey));

            if (iResult != null && fileName != null) {
                FileUtils.saveFileFromBytes(iResult, fileName);
            }
            return iResult;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return iResult;
    }


    @Override
    public BufferSize getBufferSize() {
        return BufferSize.MEDIUM;
    }

    @Override
    public Credential getCredential() {
        return null;
    }

    @Override
    public void setCredential(Credential credential) {

    }

    @Override
    public RequestConfiguration getRequestConfiguration() {
        return new RequestConfiguration();
    }

    @Override
    public void setRequestConfiguration(RequestConfiguration requestConfiguration) {

    }

    @Override
    public String getUri() {
        return null;
    }
}
