xquery version "1.0-ml";

import module namespace enrich = "org:worldbank:isearch:enrich" at "/ext/isearch/lib/enrich-lib.xqy";

declare variable $URI as xs:string external;

enrich:enrich($URI, "doc-enrich-qconsole", "false")