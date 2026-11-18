import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./app/styles/tokens.css";
import "./app/styles/global.css";
import App from "./app/App";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
