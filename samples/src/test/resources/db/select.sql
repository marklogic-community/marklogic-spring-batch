SELECT *
FROM invoice LEFT JOIN customer on invoice.customerId = customer.id
  LEFT JOIN item on invoice.id = item.invoiceId
  LEFT JOIN product on product.id = item.productId
ORDER BY invoice.id asc;