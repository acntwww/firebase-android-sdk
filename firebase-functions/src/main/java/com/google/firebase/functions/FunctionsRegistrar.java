// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.firebase.functions;

import android.content.Context;
import androidx.annotation.Keep;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.interop.InternalAppCheckTokenProvider;
import com.google.firebase.auth.internal.InternalAuthProvider;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentRegistrar;
import com.google.firebase.components.Dependency;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.platforminfo.LibraryVersionComponent;
import java.util.Arrays;
import java.util.List;

/**
 * Registers {@link FunctionsMultiResourceComponent}.
 *
 * @hide
 */
@Keep
public class FunctionsRegistrar implements ComponentRegistrar {
  private static final String LIBRARY_NAME = "fire-fn";

  @Override
  public List<Component<?>> getComponents() {
    return Arrays.asList(
        Component.builder(ContextProvider.class)
            .add(Dependency.optionalProvider(InternalAuthProvider.class))
            .add(Dependency.requiredProvider(FirebaseInstanceIdInternal.class))
            .add(Dependency.deferred(InternalAppCheckTokenProvider.class))
            .factory(
                c ->
                    new FirebaseContextProvider(
                        c.getProvider(InternalAuthProvider.class),
                        c.getProvider(FirebaseInstanceIdInternal.class),
                        c.getDeferred(InternalAppCheckTokenProvider.class)))
            .build(),
        Component.builder(FunctionsMultiResourceComponent.class)
            .name(LIBRARY_NAME)
            .add(Dependency.required(Context.class))
            .add(Dependency.required(ContextProvider.class))
            .add(Dependency.required(FirebaseApp.class))
            .factory(
                c ->
                    new FunctionsMultiResourceComponent(
                        c.get(Context.class),
                        c.get(ContextProvider.class),
                        c.get(FirebaseApp.class)))
            .build(),
        LibraryVersionComponent.create(LIBRARY_NAME, BuildConfig.VERSION_NAME));
  }
}
