define([
  "../accUtils",
  "knockout",
  "ojs/ojarraydataprovider",
  "ojs/ojknockouttemplateutils",
  "ojs/ojtable"
], function (accUtils, ko, ArrayDataProvider, KnockoutTemplateUtils) {

  function DashboardViewModel(params) {
    var self = this;
    self.statusMessage = ko.observable("Loading...");
    self.stockDataSource = ko.observable();

    self.router = params && params.router;
    if(localStorage.getItem("user")==null){self.router.go({path: 'login'})}

    self.columns = [
      { headerText: "Stock Name", field: "stockName" },
      { headerText: "Current Volume", field: "currentVolume" },
      { headerText: "Net Invested", field: "netInvested" },
      { headerText: "Current Price", field: "currentPrice" },
      { headerText: "Current Value", field: "currentValue" },
      {
        headerText: 'Sell',
         width: '250px',
        field: 'sell', // dummy field!
        renderer: KnockoutTemplateUtils.getRenderer('sellCellTemplate', true)
      }
    ];

    // Helper: track per-row sell input and state
    function enhanceStocksWithSellProps(stocks) {
      return stocks.map((stock) => {
        // Enhance each stock object with observables and methods for selling
        stock.sellMode = ko.observable(false);
        stock.sellVolume = ko.observable(1);
        stock.inputValid = ko.observable(true);
        stock.enableSellMode = function (row) {
          row.sellMode(true);
        };
        stock.disableSellMode = function (row) {
          row.sellMode(false);
          row.sellVolume(1);
          row.inputValid(true);
        };
        stock.validateSellVolume = function () {
          let max = stock.currentVolume;
          let val = Number(stock.sellVolume());
          stock.inputValid(val > 0 && val <= max);
        };
        stock.sellStock = function (row) {
          let val = Number(stock.sellVolume());
          if (!(stock.inputValid() && val > 0 && val <= stock.currentVolume))
            return;
          let customerId = self.currentCustomerId;
          let postData = {
            stockId: stock.stockId,
            customerId: customerId,
            transactionType: "SELL",
            volume: val,
          };
          fetch("http://gvpradee-fv9rjb4:8086/transaction", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(postData),
          })
            .then((resp) => resp.json())
            .then((json) => {
              if (json.status === "success") {
                self.statusMessage("Transaction successful!");
                row.sellMode(false);
                self.loadStockDetails(); // Refresh table
              } else {
                self.statusMessage("Error: " + (json.message || "Failed Sell"));
              }
            })
            .catch((err) => {
              self.statusMessage("Error: " + err.message);
            });
        };
        return stock;
      });
    }

    self.currentCustomerId = null; // Store customerId across calls

    self.loadStockDetails = function () {
      let email = localStorage.getItem("user");
      if (!email) {
        self.statusMessage("No user email found in localStorage.");
        return;
      }
      let custApiUrl = `http://gvpradee-fv9rjb4:8085/customer/${encodeURIComponent(
        email
      )}`;
      fetch(custApiUrl)
        .then((resp) => {
          if (!resp.ok) throw new Error("Failed to fetch customer");
          return resp.json();
        })
        .then((json) => {
          if (json.status !== "success")
            throw new Error(json.message || "Failed to fetch customer");
          let customerId = json.data.customerId;
          self.currentCustomerId = customerId; // Store for sell
          let stocksApiUrl = `http://gvpradee-fv9rjb4:8085/customer/stocksDetails/${customerId}`;
          return fetch(stocksApiUrl);
        })
        .then((resp) => {
          if (!resp.ok) throw new Error("Failed to fetch stocks");
          return resp.json();
        })
        .then((json) => {
          if (json.status !== "success")
            throw new Error(json.message || "Failed to fetch stocks");
          let stocksArray = json.data || [];
          self.statusMessage(`${stocksArray.length} stock(s) found.`);
          let enhancedStocks = enhanceStocksWithSellProps(stocksArray);
          self.stockDataSource(
            new ArrayDataProvider(enhancedStocks, { keyAttributes: "stockId" })
          );
        })
        .catch((err) => {
          self.statusMessage("Error: " + err.message);
          self.stockDataSource(
            new ArrayDataProvider([], { keyAttributes: "stockId" })
          ); // clear table
        });
    };

    this.connected = () => {
      accUtils.announce("Stocks page loaded.", "assertive");
      document.title = "Stocks";
      self.loadStockDetails();
    };

    this.disconnected = () => {};
    this.transitionCompleted = () => {};
  }
  return DashboardViewModel;
});
