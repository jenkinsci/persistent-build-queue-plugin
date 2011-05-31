/*
 * The MIT License
 * 
 * Copyright (c) 2011, Jesse Farinacci
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkins.ci.plugins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * This plugin allows jobs in the build queue to automatically be started if
 * Jenkins restarts.
 * 
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 * @since 1.0
 */
public final class PersistentBuildQueueBuildWrapper extends BuildWrapper {
    /**
     * Plugin marker for BuildWrapper.
     */
    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {
	@Override
	public String getDisplayName() {
	    return Messages.PersistentBuildQueueBuildWrapper_DisplayName();
	}

	@Override
	public boolean isApplicable(final AbstractProject<?, ?> item) {
	    return true;
	}
    }

    /**
     * Configuration of this plugin is per-job.
     */
    @DataBoundConstructor
    public PersistentBuildQueueBuildWrapper() {
	super();
    }

    @Override
    public BuildWrapper.Environment setUp(
	    @SuppressWarnings("rawtypes") final AbstractBuild build,
	    final Launcher launcher, final BuildListener listener)
	    throws IOException, InterruptedException {
	return new Environment() {
	    /* empty implementation */
	};
    }
}
