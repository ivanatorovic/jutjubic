import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  scenarios: {
    high_rps: {
      executor: "constant-arrival-rate",
      rate: 200,
      timeUnit: "1s",
      duration: "60s",
      preAllocatedVUs: 150,
      maxVUs: 500,
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<800"],
  },
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const PATH = __ENV.PATH || "/api/videos";

export default function () {
  const res = http.get(`${BASE_URL}${PATH}`);

  check(res, {
    "status is 200": (r) => r.status === 200,
  });


  sleep(0.01);
}
