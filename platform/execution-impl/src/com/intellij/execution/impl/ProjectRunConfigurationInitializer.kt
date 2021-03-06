// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.execution.impl

import com.intellij.execution.IS_RUN_MANAGER_INITIALIZED
import com.intellij.execution.RunManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.impl.ProjectLifecycleListener

internal class ProjectRunConfigurationInitializer(project: Project) {
  init {
    val connection = project.messageBus.connect()
    connection.subscribe(ProjectLifecycleListener.TOPIC, object : ProjectLifecycleListener {
      override fun projectComponentsInitialized(eventProject: Project) {
        if (project === eventProject) {
          requestLoadWorkspaceAndProjectRunConfiguration(project)
        }
      }
    })
  }

  private fun requestLoadWorkspaceAndProjectRunConfiguration(project: Project) {
    if (IS_RUN_MANAGER_INITIALIZED.get(project) == true) {
      return
    }

    IS_RUN_MANAGER_INITIALIZED.set(project, true)
    // we must not fire beginUpdate here, because message bus will fire queued parent message bus messages (and, so, SOE may occur because all other projectOpened will be processed before us)
    // simply, you should not listen changes until project opened
    project.service<RunManager>()
  }
}