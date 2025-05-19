WIP

[ Application Scope (Singleton Component) ]
|
v
[ Activity ViewModel (Navigation Stack Controller) ]
|
v
+-------------------------+
| Back Stack Entry A      |
|                         |
|   [ View Scope A ]      |
|        |                |
|        v                |
|  +---------------+      |
|  | State Holders |      |  (e.g., ViewModel, StateFlows)
|  +---------------+      |
|        |                |
|        v                |
|  [ CoroutineScope A ]   |
+-------------------------+
|
v
[ Composable A (reads View Scope A state) ]

