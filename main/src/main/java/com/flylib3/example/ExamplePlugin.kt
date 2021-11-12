package com.flylib3.example

import com.flylib3.FlyLibPlugin

class ExamplePlugin : FlyLibPlugin() {
    override fun onWait() {
        // Loading is Completed
        // Maybe Command Registers will be here
        super.onWait() // THIS IS NEEDED
    }

    override fun onPrepare() {
        // Prepare here for action
        // Maybe Command Executors call this
        super.onPrepare() // THIS IS NEEDED
    }

    override fun onAction() {
        // Action Started
        // Maybe Prepare Phase call this
        super.onAction() // THIS IS NEEDED
    }
}