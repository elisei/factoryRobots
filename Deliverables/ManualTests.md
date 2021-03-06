## Manual tests

### Player tests
#### Testing that walls block player movement
How: Run the application using testMap (standard), player spawns at position (2,2). <br>
Move player next to a wall section (position (3,6)) with a wall above it.
1. Expect: Key press UP keeps player in the same place due to wall <br>
Actual: Key press UP holds player in the same position (3,6).
2. Move player to position (3,7). <br>
Expect: Key press RIGHT and key press DOWN keeps player in the same position due to the wall corner <br>
Actual: Key press RIGHT and DOWN keeps the player in the same position
3. Move player to position (4,7) <br>
Expect: Key press RIGHT, UP and LEFT keeps player in the same position due to the surrounding walls <br>
Actual: Key press RIGHT, UP and LEFT keeps the player in the same position <br>
4. Move player to position (4,8) <br>
Expect: Key press DOWN and LEFT keeps player in the same position due to the surrounding walls <br>
Actual: Key press DOWN and LEFT keeps the player in the same position <br>

* All tests passed

#### Testing that player can't walk out off the map
How: Run the application using testMap (standard), player spawns at position (2,2). <br>
Move player to position (0,0), i.e. the bottom left corner.
1. Expect: Key press DOWN and LEFT keeps player in the same position due to the edge of the map.
<br> Actual: Key press DOWN and LEFT keeps player in the same position due to the edge of the map.
2. Move player to position (14,14), i.e. the top right corner. <br>
Expect: Key press UP and RIGHT keeps player in the same position due to the edge of the map.
<br> Actual: Key press UP and RIGHT keeps player in the same position due to the edge of the map.

* Both tests passed