xquery version "1.0-ml";

declare variable $URI as xs:string external;

xdmp:document-insert($URI, <goodbye>It worked</goodbye>)
