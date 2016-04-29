xquery version "1.0-ml";

declare variable $URI as xs:string external;
declare variable $COLLECTIONS as element(collections) external;

xdmp:document-set-collections($URI, $COLLECTIONS//collection/text())