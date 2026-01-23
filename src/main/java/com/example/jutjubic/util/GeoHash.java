package com.example.jutjubic.util;

import java.util.*;

public final class GeoHash {
    private GeoHash() {}

    private static final String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";

    public static String encode(double latitude, double longitude, int precision) {
        double minLat = -90.0, maxLat = 90.0;
        double minLon = -180.0, maxLon = 180.0;

        StringBuilder hash = new StringBuilder();
        boolean isEven = true;
        int bit = 0;
        int ch = 0;

        while (hash.length() < precision) {
            if (isEven) {
                double mid = (minLon + maxLon) / 2.0;
                if (longitude > mid) {
                    ch |= (1 << (4 - bit));
                    minLon = mid;
                } else {
                    maxLon = mid;
                }
            } else {
                double mid = (minLat + maxLat) / 2.0;
                if (latitude > mid) {
                    ch |= (1 << (4 - bit));
                    minLat = mid;
                } else {
                    maxLat = mid;
                }
            }

            isEven = !isEven;

            if (bit < 4) {
                bit++;
            } else {
                hash.append(BASE32.charAt(ch));
                bit = 0;
                ch = 0;
            }
        }

        return hash.toString();
    }

    /** Vraća opseg (bounding box) geohash ćelije: [minLat, maxLat, minLon, maxLon] */
    public static double[] decodeBbox(String geohash) {
        double minLat = -90.0, maxLat = 90.0;
        double minLon = -180.0, maxLon = 180.0;
        boolean isEven = true;

        for (int idx = 0; idx < geohash.length(); idx++) {
            int cd = BASE32.indexOf(geohash.charAt(idx));
            for (int i = 4; i >= 0; i--) {
                int mask = 1 << i;
                if (isEven) {
                    double mid = (minLon + maxLon) / 2.0;
                    if ((cd & mask) != 0) minLon = mid; else maxLon = mid;
                } else {
                    double mid = (minLat + maxLat) / 2.0;
                    if ((cd & mask) != 0) minLat = mid; else maxLat = mid;
                }
                isEven = !isEven;
            }
        }
        return new double[]{minLat, maxLat, minLon, maxLon};
    }

    /** Heuristika: nađi najveću preciznost gde je ćelija bar “otprilike” veličine radijusa */
    public static int choosePrecisionForRadiusKm(double lat, double lon, double radiusKm) {
        // opseg: 2..8 ti je sasvim dovoljno za ovaj projekat
        for (int p = 8; p >= 2; p--) {
            String h = encode(lat, lon, p);
            double[] bb = decodeBbox(h);

            double latSpanDeg = bb[1] - bb[0];
            double lonSpanDeg = bb[3] - bb[2];

            double latKm = latSpanDeg * 111.0;
            double lonKm = lonSpanDeg * 111.0 * Math.cos(Math.toRadians(lat));

            double cellKm = Math.min(latKm, lonKm); // konzervativno

            // želimo da 3x3 susedne ćelije sigurno pokriju krug
            // ako je jedna ćelija >= radius, onda 3 ćelije u širinu ~>= 3*radius (dovoljno)
            if (cellKm >= radiusKm) return p;
        }
        return 2;
    }

    /** 9 prefixa: centar + 8 suseda (uzimamo tačke pomerene za veličinu ćelije) */
    public static Set<String> neighborPrefixes(double lat, double lon, int precision) {
        String center = encode(lat, lon, precision);
        double[] bb = decodeBbox(center);

        double dLat = (bb[1] - bb[0]); // jedan “cell step”
        double dLon = (bb[3] - bb[2]);

        double latN = clampLat(lat + dLat);
        double latS = clampLat(lat - dLat);
        double lonE = wrapLon(lon + dLon);
        double lonW = wrapLon(lon - dLon);

        Set<String> s = new HashSet<>();
        s.add(center);
        s.add(encode(latN, lon, precision)); // N
        s.add(encode(latS, lon, precision)); // S
        s.add(encode(lat, lonE, precision)); // E
        s.add(encode(lat, lonW, precision)); // W
        s.add(encode(latN, lonE, precision)); // NE
        s.add(encode(latN, lonW, precision)); // NW
        s.add(encode(latS, lonE, precision)); // SE
        s.add(encode(latS, lonW, precision)); // SW
        return s;
    }

    private static double clampLat(double lat) {
        return Math.max(-90.0, Math.min(90.0, lat));
    }

    private static double wrapLon(double lon) {
        while (lon < -180.0) lon += 360.0;
        while (lon > 180.0) lon -= 360.0;
        return lon;
    }
}
