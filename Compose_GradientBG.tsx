"use client";

import * as React from "react";

import { motion, AnimatePresence, useReducedMotion } from "framer-motion";

export type ComposeMention = {

  id: string;

  label: string;

  sublabel?: string;

  avatar?: string;

};

export type ComposeCommand = {

  id: string;

  label: string;

  hint?: string;

  icon?: React.ReactNode;

};

export type ComposeProps = {

  value?: string;

  defaultValue?: string;

  onChange?: (value: string) => void;

  onSubmit?: (value: string) => void;

  onCommand?: (command: ComposeCommand) => void;

  mentions?: ComposeMention[];

  commands?: ComposeCommand[];

  placeholder?: string;

  maxLength?: number;

  submitLabel?: string;

  autoFocus?: boolean;

  className?: string;

  "aria-label"?: string;

};

type Trigger = { type: "@" | "/"; start: number; query: string };

const CARET_PROPS = [

  "boxSizing", "width", "height", "overflowX", "overflowY",

  "borderTopWidth", "borderRightWidth", "borderBottomWidth", "borderLeftWidth",

  "paddingTop", "paddingRight", "paddingBottom", "paddingLeft",

  "fontStyle", "fontVariant", "fontWeight", "fontStretch", "fontSize",

  "lineHeight", "fontFamily", "textAlign", "textTransform", "textIndent",

  "letterSpacing", "wordSpacing", "tabSize", "whiteSpace", "wordWrap", "wordBreak",

] as const;

function caretCoords(el: HTMLTextAreaElement, pos: number) {

  const doc = document.createElement("div");

  const s = doc.style;

  const cs = window.getComputedStyle(el);

  s.position = "absolute";

  s.visibility = "hidden";

  s.whiteSpace = "pre-wrap";

  s.wordWrap = "break-word";

  s.top = "0";

  s.left = "-9999px";

  for (const p of CARET_PROPS) {

    s[p] = cs[p];

  }

  s.height = "auto";

  s.overflow = "hidden";

  doc.textContent = el.value.slice(0, pos);

  const marker = document.createElement("span");

  marker.textContent = el.value.slice(pos) || ".";

  doc.appendChild(marker);

  document.body.appendChild(doc);

  const x = marker.offsetLeft;

  const y = marker.offsetTop;

  document.body.removeChild(doc);

  return { x, y, lineHeight: parseFloat(cs.lineHeight) || parseFloat(cs.fontSize) * 1.4 };

}

function detectTrigger(text: string, caret: number): Trigger | null {

  let i = caret - 1;

  while (i >= 0) {

    const ch = text[i];

    if (ch === "@" || ch === "/") {

      const before = i === 0 ? " " : text[i - 1];

      if (i === 0 || /\s/.test(before)) {

        const query = text.slice(i + 1, caret);

        if (!/\s/.test(query)) return { type: ch, start: i, query };

      }

      return null;

    }

    if (/\s/.test(ch)) return null;

    i--;

  }

  return null;

}

function escapeRe(s: string) {

  return s.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");

}

type Seg = { text: string; kind: "text" | "mention" | "command" };

function segment(text: string, mentionForms: string[], commandForms: string[]): Seg[] {

  const forms = [

    ...mentionForms.map((f) => ({ f, kind: "mention" as const })),

    ...commandForms.map((f) => ({ f, kind: "command" as const })),

  ].sort((a, b) => b.f.length - a.f.length);

  if (forms.length === 0) return [{ text, kind: "text" }];

  const kindOf = new Map(forms.map((x) => [x.f, x.kind]));

  const re = new RegExp(

    `(${forms.map((x) => escapeRe(x.f)).join("|")})(?=$|[^\\w])`,

    "g",

  );

  const out: Seg[] = [];

  let last = 0;

  let m: RegExpExecArray | null;

  while ((m = re.exec(text))) {

    const prev = m.index === 0 ? "" : text[m.index - 1];

    if (prev && /\w/.test(prev)) continue;

    if (m.index > last) out.push({ text: text.slice(last, m.index), kind: "text" });

    out.push({ text: m[0], kind: kindOf.get(m[0]) ?? "mention" });

    last = m.index + m[0].length;

  }

  if (last < text.length) out.push({ text: text.slice(last), kind: "text" });

  return out.length ? out : [{ text, kind: "text" }];

}

const TYPO = "text-[15px] leading-[1.6] font-normal tracking-normal";

const STYLE = `

@property --compose-a { syntax: "<angle>"; initial-value: 0deg; inherits: false; }

@property --gradient-angle { syntax: "<angle>"; initial-value: 0deg; inherits: false; }

@keyframes compose-spin { to { --compose-a: 360deg; } }

@keyframes gradient-breath {

  0%, 100% { --gradient-angle: 0deg; }

  50% { --gradient-angle: 180deg; }

}

@keyframes compose-pop {

  0%   { box-shadow: 0 0 0 3px rgba(113,113,122,.16); }

  100% { box-shadow: 0 0 0 0 rgba(113,113,122,0); }

}

.compose-ring {

  opacity: 0;

  transition: opacity .5s ease;

  background: conic-gradient(from var(--compose-a),

    rgba(161,161,170,0)   0deg,

    rgba(161,161,170,.42)  60deg,

    rgba(212,212,216,.62) 108deg,

    rgba(161,161,170,0)   168deg,

    rgba(161,161,170,0)   360deg);

}

.compose-root:focus-within .compose-ring {

  opacity: 1;

  animation: compose-spin 5s linear infinite;

}

.compose-pop { animation: compose-pop .5s ease-out; }

.compose-scroll {

  scrollbar-width: thin;

  scrollbar-color: rgba(161,161,170,.28) transparent;

}

.compose-scroll:hover, .compose-scroll:focus {

  scrollbar-color: rgba(113,113,122,.5) transparent;

}

.compose-scroll::-webkit-scrollbar { width: 11px; height: 11px; }

.compose-scroll::-webkit-scrollbar-track { background: transparent; }

.compose-scroll::-webkit-scrollbar-thumb {

  background: rgba(161,161,170,.28);

  border: 4px solid transparent;

  border-radius: 999px;

  background-clip: padding-box;

  transition: background-color .2s ease;

}

.compose-scroll:hover::-webkit-scrollbar-thumb,

.compose-scroll:focus::-webkit-scrollbar-thumb {

  background: rgba(113,113,122,.5);

  background-clip: padding-box;

}

.compose-scroll::-webkit-scrollbar-thumb:hover {

  background: rgba(82,82,91,.65);

  background-clip: padding-box;

}

.compose-gradient-bg {

  background: radial-gradient(

    ellipse at 50% 0%,

    #0066ff 0%,

    #6633ff 25%,

    #ff1493 50%,

    #ff6b35 75%,

    #000000 100%

  );

  animation: gradient-breath 8s ease-in-out infinite;

}

@media (prefers-reduced-motion: reduce) {

  .compose-root:focus-within .compose-ring { animation: none; }

  .compose-pop { animation: none; }

  .compose-gradient-bg { animation: none; }

}

`;

export function Compose({

  value,

  defaultValue = "",

  onChange,

  onSubmit,

  onCommand,

  mentions = [],

  commands = [],

  placeholder = "Write a message…  press @ to mention, / for commands",

  maxLength,

  submitLabel = "Send",

  autoFocus = false,

  className,

  "aria-label": ariaLabel = "Message",

}: ComposeProps) {

  const uid = React.useId().replace(/[:]/g, "");

  const reduce = useReducedMotion();

  const taRef = React.useRef<HTMLTextAreaElement>(null);

  const backdropRef = React.useRef<HTMLDivElement>(null);

  const pendingCaret = React.useRef<number | null>(null);

  const flashTimer = React.useRef<number | null>(null);

  const [internal, setInternal] = React.useState(defaultValue);

  const text = value ?? internal;

  React.useEffect(() => {

    if (autoFocus) {

      const ta = taRef.current;

      if (ta) {

        const end = ta.value.length;

        ta.focus();

        ta.setSelectionRange(end, end);

      }

    }

  }, [autoFocus]);

  const [trigger, setTrigger] = React.useState<Trigger | null>(null);

  const [menu, setMenu] = React.useState({ x: 0, top: 0, bottom: 0, flip: false });

  const [active, setActive] = React.useState(0);

  const [flash, setFlash] = React.useState<string | null>(null);

  const mentionForms = React.useMemo(() => mentions.map((m) => "@" + m.label), [mentions]);

  const commandForms = React.useMemo(() => commands.map((c) => "/" + c.label), [commands]);

  const segs = React.useMemo(

    () => segment(text, mentionForms, commandForms),

    [text, mentionForms, commandForms],

  );

  const popIndex = React.useMemo(() => {

    if (!flash) return -1;

    return segs.findIndex((s) => s.kind !== "text" && s.text.trim() === flash);

  }, [segs, flash]);

  const results = React.useMemo(() => {

    if (!trigger) return [] as (ComposeMention | ComposeCommand)[];

    const q = trigger.query.toLowerCase();

    if (trigger.type === "@")

      return mentions.filter((m) => m.label.toLowerCase().includes(q)).slice(0, 6);

    return commands.filter((c) => c.label.toLowerCase().includes(q)).slice(0, 6);

  }, [trigger, mentions, commands]);

  const setText = React.useCallback(

    (next: string) => {

      if (value === undefined) setInternal(next);

      onChange?.(next);

    },

    [value, onChange],

  );

  const refreshTrigger = React.useCallback(() => {

    const ta = taRef.current;

    if (!ta) return;

    const caret = ta.selectionStart ?? 0;

    const t = detectTrigger(ta.value, caret);

    setTrigger(t);

    if (t) {

      const c = caretCoords(ta, caret);

      const caretLineTop = c.y - ta.scrollTop;

      const rect = ta.getBoundingClientRect();

      const lineBottomVp = rect.top + caretLineTop + c.lineHeight;

      const flip = window.innerHeight - lineBottomVp < 252 && caretLineTop > 120;

      setMenu({

        x: c.x - ta.scrollLeft,

        top: caretLineTop + c.lineHeight + 6,

        bottom: ta.offsetHeight - caretLineTop + 6,

        flip,

      });

      setActive(0);

    }

  }, []);

  React.useLayoutEffect(() => {

    if (pendingCaret.current != null && taRef.current) {

      const pos = pendingCaret.current;

      pendingCaret.current = null;

      taRef.current.focus();

      taRef.current.setSelectionRange(pos, pos);

      refreshTrigger();

    }

  }, [text, refreshTrigger]);

  React.useEffect(() => () => { if (flashTimer.current) window.clearTimeout(flashTimer.current); }, []);

  const insert = React.useCallback(

    (choice: ComposeMention | ComposeCommand) => {

      const ta = taRef.current;

      if (!ta || !trigger) return;

      const caret = ta.selectionStart ?? 0;

      const token = (trigger.type === "@" ? "@" : "/") + choice.label;

      const next = ta.value.slice(0, trigger.start) + token + " " + ta.value.slice(caret);

      pendingCaret.current = trigger.start + token.length + 1;

      setText(next);

      setTrigger(null);

      setFlash(token);

      if (flashTimer.current) window.clearTimeout(flashTimer.current);

      flashTimer.current = window.setTimeout(() => setFlash(null), 480);

      if (trigger.type === "/") onCommand?.(choice as ComposeCommand);

    },

    [trigger, setText, onCommand],

  );

  const NAV_KEYS = ["ArrowDown", "ArrowUp", "Enter", "Tab", "Escape"];

  const onKeyUp = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {

    if (trigger && results.length && NAV_KEYS.includes(e.key)) return;

    refreshTrigger();

  };

  const onKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {

    if (trigger && results.length) {

      if (e.key === "ArrowDown") { e.preventDefault(); setActive((a) => (a + 1) % results.length); return; }

      if (e.key === "ArrowUp") { e.preventDefault(); setActive((a) => (a - 1 + results.length) % results.length); return; }

      if (e.key === "Enter" || e.key === "Tab") { e.preventDefault(); insert(results[active]); return; }

      if (e.key === "Escape") { e.preventDefault(); setTrigger(null); return; }

    }

    if (e.key === "Enter" && (e.metaKey || e.ctrlKey)) {

      e.preventDefault();

      if (text.trim()) onSubmit?.(text);

    }

  };

  const submit = () => {

    if (text.trim()) onSubmit?.(text);

  };

  const overLimit = maxLength != null && text.length > maxLength;

  return (

    <div className={["compose-root relative isolate w-full", className ?? ""].join(" ")}>

      <style>{STYLE}</style>

      <div aria-hidden className="compose-ring pointer-events-none absolute -inset-[2px] -z-10 rounded-[18px] blur-[2px]" />

      <div className="compose-gradient-bg relative rounded-2xl shadow-[0_1px_2px_rgba(24,24,27,0.04),0_16px_40px_-24px_rgba(24,24,27,0.22)]">

        <div className="relative">

          <div

            ref={backdropRef}

            aria-hidden

            className={["pointer-events-none absolute inset-0 overflow-hidden whitespace-pre-wrap break-words px-4 py-3.5 text-transparent", TYPO].join(" ")}

          >

            {segs.map((s, i) => {

              if (s.kind === "text") return <span key={i}>{s.text}</span>;

              return (

                <span

                  key={i}

                  className={[

                    "box-decoration-clone rounded-[5px] py-[3px] bg-white/[0.15]",

                    i === popIndex ? "compose-pop" : "",

                  ].join(" ")}

                >

                  {s.text}

                </span>

              );

            })}

            {"\n"}

          </div>

          <textarea

            ref={taRef}

            value={text}

            rows={3}

            maxLength={maxLength}

            placeholder={placeholder}

            aria-label={ariaLabel}

            role="combobox"

            aria-expanded={!!(trigger && results.length)}

            aria-controls=`${uid}-list`}

            aria-activedescendant={trigger && results.length ? `${uid}-opt-${active}` : undefined}

            spellCheck

            onChange={(e) => setText(e.target.value)}

            onKeyDown={onKeyDown}

            onKeyUp={onKeyUp}

            onClick={refreshTrigger}

            onScroll={(e) => {

              if (backdropRef.current) {

                backdropRef.current.scrollTop = e.currentTarget.scrollTop;

                backdropRef.current.scrollLeft = e.currentTarget.scrollLeft;

              }

            }}

            onBlur={() => setTimeout(() => setTrigger(null), 120)}

            className={[

              "compose-scroll relative block max-h-64 min-h-[104px] w-full resize-none bg-transparent px-4 py-3.5 text-white caret-white outline-none placeholder:text-white/60",

              TYPO,

            ].join(" ")}

          />

          <AnimatePresence>

            {trigger && results.length > 0 && (

              <motion.ul

                key="picker"

                id=`${uid}-list`}

                role="listbox"

                initial={reduce ? { opacity: 0 } : { opacity: 0, y: menu.flip ? 6 : -6, scale: 0.97 }}

                animate={reduce ? { opacity: 1 } : { opacity: 1, y: 0, scale: 1 }}

                exit={reduce ? { opacity: 0 } : { opacity: 0, y: menu.flip ? 4 : -4, scale: 0.98 }}

                transition={reduce ? { duration: 0.12 } : { type: "spring", stiffness: 620, damping: 36, mass: 0.6 }}

                className="compose-scroll absolute z-30 max-h-[236px] w-[236px] overflow-auto rounded-xl border border-zinc-200/80 bg-white/95 p-1 shadow-[0_10px_36px_-10px_rgba(24,24,27,0.3)] backdrop-blur-xl"

                style={{

                  left: Math.max(8, menu.x),

                  transformOrigin: menu.flip ? "bottom left" : "top left",

                  ...(menu.flip ? { bottom: menu.bottom } : { top: menu.top }),

                }}

              >

                {results.map((r, i) => {

                  const isMention = trigger.type === "@";

                  const m = r as ComposeMention;

                  const c = r as ComposeCommand;

                  const on = i === active;

                  return (

                    <li key={r.id} role="option" id=`${uid}-opt-${i}`} aria-selected={on} className="relative">

                      {on && (

                        <motion.div

                          layoutId=`${uid}-hl`}

                          className="absolute inset-0 rounded-lg bg-zinc-100"

                          transition={reduce ? { duration: 0 } : { type: "spring", stiffness: 650, damping: 40, mass: 0.5 }}

                        />

                      )}

                      <button

                        type="button"

                        tabIndex={-1}

                        onMouseEnter={() => setActive(i)}

                        onMouseDown={(e) => { e.preventDefault(); insert(r); }}

                        className="relative z-10 flex w-full items-center gap-2.5 rounded-lg px-2 py-1.5 text-left"

                      >

                        {isMention ? (

                          <>

                            <Avatar mention={m} />

                            <span className="min-w-0 flex-1 leading-tight">

                              <span className="block truncate text-[13px] font-medium text-zinc-800">

                                {m.label}

                              </span>

                              {m.sublabel && (

                                <span className="block truncate text-[11px] text-zinc-400">

                                  {m.sublabel}

                                </span>

                              )}

                            </span>

                          </>

                        ) : (

                          <>

                            <span className="flex h-[26px] w-[26px] flex-none items-center justify-center rounded-[8px] bg-gradient-to-b from-zinc-50 to-zinc-100 text-zinc-700 shadow-sm ring-1 ring-inset ring-zinc-900/[0.08] [&>svg]:h-4 [&>svg]:w-4">

                              {c.icon ?? <span className="font-mono text-[13px] font-medium">/</span>}

                            </span>

                            <span className="min-w-0 flex-1 leading-tight">

                              <span className="block truncate text-[13px] font-medium text-zinc-800">

                                {c.label}

                              </span>

                              {c.hint && (

                                <span className="block truncate text-[11px] text-zinc-400">

                                  {c.hint}

                                </span>

                              )}

                            </span>

                          </>

                        )}

                        <span className={["flex-none transition-opacity", on ? "opacity-100" : "opacity-0"].join(" ")}>

                          <Kbd>↵</Kbd>

                        </span>

                      </button>

                    </li>

                  );

                })}

              </motion.ul>

            )}

          </AnimatePresence>

        </div>

        <div className="flex items-center justify-between gap-2 border-t border-white/20 px-3 py-2.5">

          <div className="flex items-center gap-1.5 text-[11px] text-white/70">

            <Kbd>@</Kbd><span>mention</span>

            <span className="mx-0.5 text-white/40">·</span>

            <Kbd>/</Kbd><span>commands</span>

          </div>

          <div className="flex items-center gap-3">

            {maxLength != null && text.length > 0 && <CounterRing value={text.length} max={maxLength} />}

            <button

              type="button"

              onClick={submit}

              disabled={!text.trim() || overLimit}

              className="group inline-flex items-center gap-2 rounded-xl bg-white px-3.5 py-2 text-[13px] font-semibold text-zinc-900 shadow-sm transition-all duration-150 hover:bg-zinc-100 active:scale-[0.97] disabled:pointer-events-none disabled:opacity-40"

            >

              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" className="transition-transform duration-200 group-hover:-translate-y-0.5 group-hover:translate-x-0.5" aria-hidden>

                <path d="M22 2 11 13M22 2l-7 20-4-9-9-4 20-7Z" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round" />

              </svg>

              {submitLabel}

              <span className="ml-0.5 hidden items-center gap-0.5 opacity-60 sm:inline-flex">

                <Kbd tone="invert">⌘</Kbd><Kbd tone="invert">↵</Kbd>

              </span>

            </button>

          </div>

        </div>

      </div>

    </div>

  );

}

function CounterRing({ value, max }: { value: number; max: number }) {

  const pct = Math.min(1, value / max);

  const r = 7;

  const circ = 2 * Math.PI * r;

  const over = value > max;

  const near = value >= max * 0.8;

  const color = over ? "#ef4444" : near ? "#f59e0b" : "#ffffff";

  const remaining = max - value;

  return (

    <span className="flex items-center gap-1.5">

      {value / max >= 0.86 && (

        <span className="tabular-nums text-[11px] font-medium text-white" style={{ color }}>

          {remaining}

        </span>

      )}

      <svg width="18" height="18" viewBox="0 0 18 18" className="-rotate-90">

        <circle cx="9" cy="9" r={r} fill="none" strokeWidth="2" className="stroke-white/20" />

        <circle

          cx="9" cy="9" r={r} fill="none" strokeWidth="2" stroke={color} strokeLinecap="round"

          strokeDasharray={circ} strokeDashoffset={circ * (1 - pct)}

          style={{ transition: "stroke-dashoffset .25s ease, stroke .25s ease" }}

        />

      </svg>

    </span>

  );

}

function Avatar({ mention }: { mention: ComposeMention }) {

  if (mention.avatar) {

    return (

      <img

        src={mention.avatar}

        alt=""

        aria-hidden

        className="h-6 w-6 flex-none rounded-full object-cover ring-1 ring-black/[0.06]"

      />

    );

  }

  const parts = mention.label.trim().split(/\s+/);

  const initials = (parts.length === 1 ? parts[0].slice(0, 2) : parts.slice(0, 2).map((w) => w[0]).join("")).toUpperCase();

  return (

    <span className="flex h-6 w-6 flex-none items-center justify-center rounded-full bg-white/20 text-[10px] font-semibold text-white ring-1 ring-white/30">

      {initials}

    </span>

  );

}

function Kbd({ children, tone }: { children: React.ReactNode; tone?: "invert" }) {

  return (

    <kbd

      className={[

        "inline-flex h-4 min-w-4 items-center justify-center rounded border px-1 font-sans text-[10px] leading-none",

        tone === "invert"

          ? "border-white/25 bg-white/10 text-white/70"

          : "border-white/30 bg-white/15 text-white/80",

      ].join(" ")}

    >

      {children}

    </kbd>

  );

}

export default Compose;
