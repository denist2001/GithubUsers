* Project description

This project includes several widely used libraries and shows how to deal with them in connection.

Project created as multilayer project with domain, presentation and repository layer.
Why different layers:
1. We can split the logic of the app
2. We can do tasks of the same feature in parallel
3. We can unbind network and presentation layers
4. Code became more testable
5. Different layers can be put in separate modules
6. Easier to inject objects

Project uses Hilt as dependency injection tool.
Why Hilt:
1. Recommended by Google
2. Extension of well-known Dagger
3. Easier in testing
4. Can catch errors on build step

Project uses navigation for transitions between the fragments.
Why navigation:
1. Easier way to manage transitions between fragments
2. Easier way to pass parameters between fragments
3. More predictable behavior base on visualization
4. Navigation controller can be found in any place(for example it's possible to navigate from the ViewModel)

Project uses Coil for image presentation.
Why Coil:
1. Written on Kotlin and based on coroutines
2. Adds to final build less methods
3. Takes care about caching of images
4. Has reach image-transformation possibilities and another functionality

To provide access to the view inside fragments and activity was used view binding.
Why view binding:
1. Easy to get access to any view
2. Works faster then findViewById()
3. Reducing the number of internal variables inside classes

All this allow me to create clean and transparent code where easy to find any logic and write tests for any part of code.
Click event flow implemented with custom solution, probably already exist recommended way (needs to be investigated).
Couple of places still have to be changed:
1. Not implemented possibility to put in DiffUtils parameter to sort items in the list.
2. Not finished instrumentation tests to check network use-cases and some UI use-cases.
3. Would be nice to scroll to last clicked position after user returns from details fragments.