import type { ReactNode } from "react";
import "./styles/app-shell.css";
import TopBar from "./TopBar";
import SpaceBackdrop from "./SpaceBackdrop";

export default function AppShell({ children }: { children: ReactNode }) {
  return (
    <div className="app-shell">
      <TopBar />
      <SpaceBackdrop />
      <main>{children}</main>
    </div>
  );
}
