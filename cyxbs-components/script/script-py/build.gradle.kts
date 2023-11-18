import com.chaquo.python.PythonExtension

plugins {
  id("module-manager")
  id("com.chaquo.python") version "14.0.2"
}

dependModule {
  dependBase()
}

dependLibrary {
}

android {
  defaultConfig {
    (this as ExtensionAware).extensions.configure<PythonExtension> {
      pip.run {
        install("requests")
        install("lxml")
      }
    }
    ndk {
      abiFilters += listOf("arm64-v8a")
    }
  }
}