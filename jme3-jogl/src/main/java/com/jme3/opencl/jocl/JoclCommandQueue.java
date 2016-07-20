/*
 * Copyright (c) 2009-2016 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.opencl.jocl;

import com.jme3.opencl.CommandQueue;
import com.jme3.opencl.OpenCLObjectManager;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLPlatform;
import com.jogamp.opencl.llb.CL;
import com.jogamp.opencl.llb.CLCommandQueueBinding;

/**
 *
 * @author shaman
 */
public class JoclCommandQueue extends CommandQueue {

    final CL cl;
    final long id;

    public JoclCommandQueue(long id) {
        super(new ReleaserImpl(id));
        this.id = id;
        this.cl = CLPlatform.getLowLevelCLInterface();
    }
    
    @Override
    public void flush() {  
        int ret = cl.clFlush(id);
        Utils.checkError(ret, "clFlush");
    }

    @Override
    public void finish() {
        int ret = cl.clFinish(id);
        Utils.checkError(ret, "clFinish");
    }
    
    private static class ReleaserImpl implements ObjectReleaser {
        private long id;

        private ReleaserImpl(long id) {
            this.id = id;
        }
        
        @Override
        public void release() {
            if (id != 0) {
                int ret = CLPlatform.getLowLevelCLInterface().clReleaseCommandQueue(id);
                id = 0;
                Utils.reportError(ret, "clReleaseCommandQueue");
            }
        }
    }
}
