xquery version "1.0-ml";

declare namespace sec = "http://marklogic.com/xdmp/security";

declare variable $URI as xs:string external;
declare variable $DOC as element() external;
declare variable $COLLECTIONS as element(collections) external;
declare variable $PERMISSIONS as element(permissions) external;

xdmp:document-insert(
  $URI, 
  $DOC, 
  ($PERMISSIONS/sec:permission), 
  $COLLECTIONS//collection/text()
)