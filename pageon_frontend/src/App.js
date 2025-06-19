import React, { useState, useEffect } from "react";
import "./App.css";
import axios from "axios";

function App() {
  const [email, setemail] = useState();
  const [psw, setpsw] = useState();
  const [testStr, setTestStr] = useState("");

  function callback(str) {
      setTestStr(str);
  }

  useEffect(() => {
      axios
          .get("/api/test")
          .then((Response) => {
              callback(Response.data);
          })
          .catch((Error) => {
              console.log(Error);
          });
  }, []);

  return (
      <div className="App">
          <div>
              api/test == {">"}
              {testStr}
          </div>
      </div>
  );
}

export default App;