package com.icuxika.framework.oss.core;

import com.icuxika.framework.oss.local.LocalProperties;
import com.icuxika.framework.oss.remote.RemoteProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileProperties {

    private LocalProperties local;

    private RemoteProperties remote;

    public LocalProperties getLocal() {
        return local;
    }

    public void setLocal(LocalProperties local) {
        this.local = local;
    }

    public RemoteProperties getRemote() {
        return remote;
    }

    public void setRemote(RemoteProperties remote) {
        this.remote = remote;
    }
}
